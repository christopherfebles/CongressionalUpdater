package com.congressionalphotodirectory.main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.congressionalphotodirectory.data.Legislator;
import com.congressionalphotodirectory.sql.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;

public class PhotoDownloader {

	private static final String WIKIPEDIA_ENDPOINT = "http://en.wikipedia.org/w/api.php";
	
	/**
	 * Get the photo for the given legislator, either from database cache, or downloaded from MediaWiki API. 
	 * 
	 * @param legislator	The legislator for whom to get the photo
	 * @return				The raw photograph
	 */
	public BufferedImage getPhoto ( Legislator legislator ) {
		
		BufferedImage photo = Database.loadLegislatorImage(legislator);
		
		if ( photo == null )
			photo = this.loadPhoto( legislator );
//		else
//			System.out.println( "Loaded image from database cache for: " + legislator.getFirst_name() + " " + legislator.getLast_name() );
		
		return photo;
	}
	
	/**
	 * Loads the (hopefully) correct photo URL into the given Legislator's {@link Legislator#setPhotoUrl(String)}.
	 * <br>
	 * Caches this photo into the database.
	 * 
	 * @param legislator	The legislator whose image we're attempting to load
	 */
	private BufferedImage loadPhoto( Legislator legislator ) {
		
		JsonArray imageResultsArray = getImageSearchResults( legislator );
		
		if ( imageResultsArray == null || imageResultsArray.isJsonNull() ) {
			System.err.println( "NO IMAGE FOUND for: " + legislator.getFirst_name() + " " + legislator.getLast_name() );
			return null;
		}

		String queryStr = "?action=query&format=json&maxlag=5&prop=imageinfo&format=json&iiprop=url%7Csize&iilimit=10&titles=";
		String titleStr = "";
		
		for ( int x = 0; x < imageResultsArray.size(); x++ ) {
			String title = imageResultsArray.get(x).getAsJsonObject().get( "title" ).getAsString();
			if ( !StringUtils.isEmpty(titleStr) )
				titleStr += "|";
			titleStr += title;
		}
		if ( StringUtils.isEmpty(titleStr) ) {
			System.err.println( "Title search string empty for: " + legislator.getFirst_name() + " " + legislator.getLast_name() );
			return null;
		}
		
		queryStr += titleStr;
		String url = WIKIPEDIA_ENDPOINT + queryStr;
		JsonElement results = getMediaWikiURL( url );

		JsonObject resultObj = results.getAsJsonObject().get("query").getAsJsonObject();
		JsonObject pages = resultObj.get("pages").getAsJsonObject();
		
		int size = 0;
		String imageUrl = "";

		Set<Entry<String, JsonElement>> pageSet = pages.entrySet();
		for (Entry<String, JsonElement> entry : pageSet ) {
			JsonElement value = entry.getValue();
			JsonElement imageInfoElement = value.getAsJsonObject().get("imageinfo");
			
			if ( imageInfoElement == null || imageInfoElement.isJsonNull() ) {
				continue;
			}
			
			JsonObject imageInfo = imageInfoElement.getAsJsonArray().get(0).getAsJsonObject();
			String tempImageUrl = imageInfo.get("url").getAsString(); 
			if ( isImageUrl(tempImageUrl) ) {
				int tempSize = imageInfo.get("size").getAsInt();
				if ( tempSize > size ) {
					size = tempSize;
					imageUrl = tempImageUrl;
				}
			}
		}
		
		legislator.setPhotoUrl(imageUrl);
		BufferedImage photo = this.downloadPhotoUrl(imageUrl, legislator);
		
		return photo;
	}
	
	/**
	 * Downloads the given photo and saves it to the database cache
	 * 
	 * @param photoUrl		The URL of the photo to download
	 * @param legislator	The legislator whose photo this is
	 * @return				The downloaded photo
	 */
	public BufferedImage downloadPhotoUrl( String photoUrl, Legislator legislator ) {
		
		BufferedImage photo = null;
		try {
			System.out.println( "Downloading " + photoUrl );
			photo = ImageIO.read( new URL(photoUrl) );
			
		} catch ( IOException e ) {
			System.err.println( "Error downloading image for " + legislator.getFirst_name() + " " + legislator.getLast_name() + ": " + photoUrl);
			System.err.println( "Attempting JPEGDecoder" );
			try {
				photo = JPEGCodec.createJPEGDecoder(new URL(photoUrl).openStream()).decodeAsBufferedImage();
			} catch (ImageFormatException | IOException e1) {
				System.err.println( "Unable to decode URL." );
				e.printStackTrace();
				throw new RuntimeException( e1 );
			}
		}
		
		System.out.println( "Saving photo to database cache..." );
		Database.writeLegislatorToDatabase(legislator, photo);
		
		return photo;
	}
	
	/**
	 * Search MediaWiki for images related to the given legislator.
	 * <br><br>
	 * If the inital result is null, loop through all possible search strings defined in {@link #generateSearchStrings(Legislator)}
	 * 
	 * @param legislator	The legislator for whom to load a photo for
	 * @return				A JsonArray containing the search results from MediaWiki
	 */
	private JsonArray getImageSearchResults( Legislator legislator ) {
		
		JsonArray retVal = null;
		LinkedList<String> searchStrings = generateSearchStrings( legislator );

		for ( String searchStr : searchStrings ) {
			retVal = getImageSearchResults(searchStr);
			if ( retVal != null && !retVal.isJsonNull() ) {
				break;
			}
		}
		
		return retVal;
	}
	
	/**
	 * Searches MediaWiki for images with the given search string only.
	 * 
	 * @see #getImageSearchResults(Legislator)
	 * @param searchStr		The search string to use
	 * @return				A JsonArray containing the results of the search
	 */
	private JsonArray getImageSearchResults( String searchStr ) {
		
		String queryString = "?action=query&list=search&format=json&srnamespace=6&srwhat=text&srlimit=10&maxlag=5";
		String url = WIKIPEDIA_ENDPOINT + queryString + "&srsearch=" + searchStr;
		
		JsonArray retVal = null;
		JsonElement results = getMediaWikiURL( url );
		
		JsonObject resultObj = results.getAsJsonObject().get("query").getAsJsonObject();

		int numResults = resultObj.get("searchinfo").getAsJsonObject().get("totalhits").getAsInt();
		if ( numResults > 0 ) {
			retVal = resultObj.get("search").getAsJsonArray();
		}
		
		return retVal;
	}
	
	/**
	 * Determines file type by examining the extension. PNG, JPG, and JPEG indicate image files.
	 * <br><br>
	 * Some "images" were coming back of unknown type, or PDF
	 * 
	 * @param imageUrl	URL string for the image we're checking
	 * @return			If this string points to an expected image type
	 */
	private boolean isImageUrl( String imageUrl ) {
		String fileExtension = "";
		if ( imageUrl.contains(".") )
			fileExtension = imageUrl.substring( imageUrl.lastIndexOf(".") ).toLowerCase();

		return fileExtension.contains("png") || fileExtension.contains("jpg") || fileExtension.contains("jpeg");
	}
	
	private JsonElement getMediaWikiURL( String urlStr ) {

		urlStr = urlStr.replace( "  ", " " );
		urlStr = urlStr.replace( " ", "%20" );
		JsonElement results = null;
		boolean gotResults = false;
		
		do { 
			InputStream is = null;
			JsonParser parser = new JsonParser();
		
			try {
				//MediaWiki requires a custom User Agent
				URL url = new URL(urlStr);
			    URLConnection conn = url.openConnection();
			    conn.addRequestProperty("User-Agent", Configuration.getConfiguration().getProperty("user.agent"));
	
			    is = conn.getInputStream();
				results = parser.parse(new InputStreamReader(is, Charset.forName("UTF-8")));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try { is.close(); } catch (IOException e) {}
			}
			
			gotResults = results != null && results.getAsJsonObject() != null && 
					results.getAsJsonObject().get("query") != null && 
					results.getAsJsonObject().get("query").getAsJsonObject() != null;
			
			if ( !gotResults ) {
				//Wait before retrying again
				System.err.println( "Results of image search are null. Re-trying in five seconds..." );
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
			}
		} while ( !gotResults );
		
		return results;
	}
	
	/**
	 * Convert accented characters to their "normal" alphabetical letters.
	 * 
	 * @see http://stackoverflow.com/questions/1008802/converting-symbols-accent-letters-to-english-alphabet
	 * @param 	str		The string to convert
	 * @return			The converted String
	 */
	private String deAccent(String str) {
		if ( str == null ) return "";
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	/**
	 * Creates a list of possible search strings for MediaWiki API. This allows the use of a loop rather than nested if-thens.
	 * <br><br>
	 * Implemented searches:<br>
	 * <br>
	 * Congress/Senate <full name> Official<br>
	 * Sen/Rep <full name> Official<br>
	 * <br>
	 * Congress/Senate <nick name> Official<br>
	 * Sen/Rep <nick name> Official<br>
	 * <br>
	 * <full name> Official<br>
	 * <br>
	 * Congress/Senate <last name> Official<br>
	 * Sen/Rep <last name> Official<br>
	 * <br>
	 * Congress/Senate <full name><br>
	 * Sen/Rep <full name><br>
	 * <br>
	 * <full name><br>
	 * 
	 * @param legislator	The Legislator whose photo we're looking for
	 * @return				A list of possible search strings
	 */
	private LinkedList<String> generateSearchStrings( Legislator legislator ) {
		
		LinkedList<String> retVal = new LinkedList<String>();
		//Escape unicode characters
		//I'm not always using the escaped names, because the Wikipedia links don't always use escaped names. Sigh.
		String escapedFirstName = StringEscapeUtils.escapeJava(legislator.getFirst_name());
		String escapedLastName = StringEscapeUtils.escapeJava(legislator.getLast_name());
		String escapedNickname = StringEscapeUtils.escapeJava(legislator.getNickname());
		//And sometimes they ignore the special characters completely
		String replacedFirstName = deAccent(legislator.getFirst_name());
		String replacedLastName = deAccent(legislator.getLast_name());
		String replacedNickname = deAccent(legislator.getNickname());
		
		retVal.add( "Congress " + legislator.getFirst_name() + " " + legislator.getLast_name() + " Official" );
		retVal.add( legislator.getTitle() + " " + legislator.getFirst_name() + " " + legislator.getLast_name() + " Official" );
		retVal.add( "Congress " + escapedFirstName + " " + escapedLastName + " Official" );
		retVal.add( legislator.getTitle() + " " + escapedFirstName + " " + escapedLastName + " Official" );
		retVal.add( "Congress " + replacedFirstName + " " + replacedLastName + " Official" );
		retVal.add( legislator.getTitle() + " " + replacedFirstName + " " + replacedLastName + " Official" );
		
		if ( legislator.isSenator() ) {
			retVal.add( "Senate " + legislator.getFirst_name() + " " + legislator.getLast_name() + " Official" );
			retVal.add( "Senate " + escapedFirstName + " " + escapedLastName + " Official" );
			retVal.add( "Senate " + replacedFirstName + " " + replacedLastName + " Official" );
		}
		
		if ( !StringUtils.isEmpty( legislator.getNickname() ) ) {
			retVal.add("Congress " + legislator.getNickname() + " " + legislator.getLast_name() + " Official");
			retVal.add( legislator.getTitle() + " " + legislator.getNickname() + " " + legislator.getLast_name() + " Official" );
			
			retVal.add("Congress " + escapedNickname + " " + escapedLastName + " Official");
			retVal.add( legislator.getTitle() + " " + escapedNickname + " " + escapedLastName + " Official" );
			
			retVal.add("Congress " + replacedNickname + " " + replacedLastName + " Official");
			retVal.add( legislator.getTitle() + " " + replacedNickname + " " + replacedLastName + " Official" );
			
			if ( legislator.isSenator() ) {
				retVal.add("Senate " + legislator.getNickname() + " " + legislator.getLast_name() + " Official");
				retVal.add("Senate " + escapedNickname + " " + escapedLastName + " Official");
				retVal.add("Senate " + replacedNickname + " " + replacedLastName + " Official");
			}
		}
		
		retVal.add( legislator.getFirst_name() + " " + legislator.getLast_name() + " Official" );
		retVal.add( escapedFirstName + " " + escapedLastName + " Official" );
		retVal.add( replacedFirstName + " " + replacedLastName + " Official" );
		
		retVal.add("Congress " + legislator.getLast_name() + " Official");
		retVal.add("Congress " + escapedLastName + " Official");
		retVal.add("Congress " + replacedLastName + " Official");
		
		if ( legislator.isSenator() ) {
			retVal.add("Senate " + legislator.getLast_name() + " Official");
			retVal.add("Senate " + escapedLastName + " Official");
			retVal.add("Senate " + replacedLastName + " Official");
		}
		
		retVal.add( legislator.getTitle() + " " + legislator.getLast_name() + " Official" );
		retVal.add( legislator.getTitle() + " " + escapedLastName + " Official" );
		retVal.add( legislator.getTitle() + " " + replacedLastName + " Official" );
		
		retVal.add( "Congress " + legislator.getFirst_name() + " " + legislator.getLast_name() );
		retVal.add( "Congress " + escapedFirstName + " " + escapedLastName );
		retVal.add( "Congress " + replacedFirstName + " " + replacedLastName );
		
		if ( legislator.isSenator() ) {
			retVal.add( "Senate " + legislator.getFirst_name() + " " + legislator.getLast_name() );
			retVal.add( "Senate " + escapedFirstName + " " + escapedLastName );
			retVal.add( "Senate " + replacedFirstName + " " + replacedLastName );
		}
		
		retVal.add( legislator.getTitle() + " " + legislator.getFirst_name() + " " + legislator.getLast_name() );
		retVal.add( legislator.getTitle() + " " + escapedFirstName + " " + escapedLastName );
		retVal.add( legislator.getTitle() + " " + replacedFirstName + " " + replacedLastName );
		
		retVal.add( legislator.getFirst_name() + " " + legislator.getLast_name() );
		retVal.add( escapedFirstName + " " + escapedLastName );
		retVal.add( replacedFirstName + " " + replacedLastName );
		
		return retVal;
	}

}
