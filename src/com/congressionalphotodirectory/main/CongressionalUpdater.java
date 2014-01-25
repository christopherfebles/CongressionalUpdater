package com.congressionalphotodirectory.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.congressionalphotodirectory.data.Committee;
import com.congressionalphotodirectory.data.Legislator;
import com.congressionalphotodirectory.data.Legislator.Legislators;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CongressionalUpdater {
	
	private static final String LOCAL_PATH = "";
	private static final int ITEMS_PER_PAGE = 50;
	
	/**
	 * Load legislators and print XML
	 * 
	 * @param args				Command Line Arguments are ignored
	 * @throws JAXBException 	Thrown if there's an error generating XML
	 * @throws IOException 		Thrown if there's an error writing to disk
	 */
	public static void main(String[] args) throws IOException, JAXBException {
		CongressionalUpdater self = new CongressionalUpdater();
		List<Legislator> senators = self.getLegislators(true);
		List<Legislator> representatives = self.getLegislators(false);
		
		self.writeLegislatorData(senators, "senators.xml");
		self.writeLegislatorData(representatives, "representatives.xml");
	}
	
	public List<Legislator> getLegislators( boolean senate ) {
		
		String chamber = "house";
		if ( senate ) {
			chamber = "senate";
		}
		
		String url = Configuration.getConfiguration().getProperty("sunlight.domain") + "/legislators?per_page=" + ITEMS_PER_PAGE + 
				"&chamber=" + chamber + 
				"&apikey=" + Configuration.getConfiguration().getProperty("sunlight.api.key");
		return this.loadData(url, null);
	}
	
	public void loadCommitteeData ( Legislator member ) {
		
		String url = Configuration.getConfiguration().getProperty("sunlight.domain") + "/committees?per_page=" + ITEMS_PER_PAGE + 
				"&apikey=" + Configuration.getConfiguration().getProperty("sunlight.api.key") + 
				"&fields=name,committee_id,chamber,url,office,phone,subcommittee,subcommittees,parent_committee_id,parent_committee" +  //,members" +
				"&member_ids=" + member.getBioguide_id();
		
		this.loadData(url, member);
	}

	/**
	 * Write the given list of Legislators to the given filename in XML format.
	 * 
	 * @param members			The list of legislators to write
	 * @param fileName			The external XML file - {@link #LOCAL_PATH} will be prepended to this string
	 * @throws IOException		Thrown if there is a file write error
	 * @throws JAXBException	Thrown if there is an error generating XML
	 */
	public void writeLegislatorData( List<Legislator> members, String fileName ) throws IOException, JAXBException {
		
		File destFile = new File(LOCAL_PATH + fileName);

		//@see: http://theopentutorials.com/tutorials/java/jaxb/jaxb-marshalling-and-unmarshalling-list-of-objects/
		JAXBContext context;
        FileOutputStream outStream = new FileOutputStream( destFile );
        OutputStreamWriter writer = new OutputStreamWriter( outStream, "UTF-8" );
        
//        BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
        
        context = JAXBContext.newInstance(Legislators.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(new Legislators(members), writer);
        writer.close();
		
	}
	
	/**
	 * Loads data from the given url. 
	 * If member is non-null, assume we're loading Committee objects, and add them to the given Legislator.
	 * Otherwise, assume we're loading Legislator objects.
	 * <br>
	 * <br>
	 * It bothered me having two methods to do essentially the same thing, so I created this joint method.<br>
	 * I thought about using generics, but with only two code paths, I rewrote it as an if statement instead.
	 * 
	 * @param url			The url to load data from
	 * @param member		The member to operate on, or null
	 * @return				A list of loaded legislators, or an empty list
	 */
	private List<Legislator> loadData( String url, Legislator member ) {
		
		InputStream is = null;
		JsonParser parser = new JsonParser();
		JsonElement results = null;
		List<Legislator> legList = new ArrayList<Legislator>();
		boolean isCommittee = member != null;
		PhotoDownloader photoDownloader = new PhotoDownloader();
		
		int page = 1;
		boolean notDone = true;
		do {
			String pagedURL = url;
			if ( page > 1 ) {
				pagedURL = pagedURL + "&page=" + page;
			}
			try {
				is = new URL(pagedURL).openStream();
				results = parser.parse(new InputStreamReader(is, Charset.forName("UTF-8")));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try { is.close(); } catch (IOException e) {}
			}

			JsonObject resultObj = results.getAsJsonObject();
			int numResults = resultObj.get("count").getAsInt();
			
			JsonArray resultArray = resultObj.get("results").getAsJsonArray();
			Gson gson = new Gson();
			for ( int x = 0; x < resultArray.size(); x++ ) {
				
				if ( isCommittee ) {
					Committee committee = gson.fromJson(resultArray.get(x), Committee.class);
					member.addCommittee(committee);
				} else {
					Legislator legislator = gson.fromJson(resultArray.get(x), Legislator.class);
					this.loadCommitteeData(legislator);
					
					BufferedImage photo = photoDownloader.getPhoto(legislator);

					this.writePhotos(legislator, photo);
					
					legList.add(legislator);
				}
			}
			
			notDone = page*ITEMS_PER_PAGE < numResults;
			page++;
			
		} while ( notDone );
		return legList;
	}
	
	private void writePhotos( Legislator legislator, BufferedImage photo ) {
		
		String filetype = "jpg";
		String filename = "Photos/" + legislator.getBioguide_id() + "." + filetype;
		File file = new File( filename );
		if ( file.exists() )
			file.delete();
		
		//Resize image for iPhone and save
		BufferedImage iPhoneImage = ImageEditor.generateIPhoneImage(photo);
		try {
			ImageIO.write( iPhoneImage, filetype, new File(filename) );
		} catch (IOException e) {
			System.err.println("Unable to write iPhone image to disk: " + filename);
		}
		
		//Create thumbnail image for back view
		filename = "Photos/" + legislator.getBioguide_id() + "_thumb." + filetype;
		file = new File( filename );
		if ( file.exists() )
			file.delete();
		
		BufferedImage thumbnail = ImageEditor.generateThumbnail(photo);
		try {
			ImageIO.write( thumbnail, filetype, new File(filename) );
		} catch (IOException e) {
			System.err.println("Unable to write thumbnail image to disk: " + filename);
		}
	}
	
}
