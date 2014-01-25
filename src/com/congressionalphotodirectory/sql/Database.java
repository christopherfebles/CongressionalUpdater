package com.congressionalphotodirectory.sql;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.congressionalphotodirectory.data.Legislator;
import com.congressionalphotodirectory.main.Configuration;
import com.congressionalphotodirectory.main.PhotoDownloader;

public class Database {
	
	private static final String DB_USER = "database.user";
	private static final String DB_PASS = "database.password";
	private static final String DB_CONN = "database.connection.string";

	/**
	 * Insert or update the legislator in the database with the given photo.
	 * 
	 * @param legislator	The legislator to update: {@link Legislator#getPhotoUrl()} CANNOT be null
	 * @param photo			The photo to save
	 * @return				True if the database update was successful, false otherwise
	 */
	public static boolean writeLegislatorToDatabase( Legislator legislator, BufferedImage photo ) {
		
		String sql = "Insert Into Congress( bioguide_id, first_name, last_name, photo_url, photo )"
				+ " Values ( ?, ?, ?, ?, ? ) On Duplicate Key "
				+ " Update first_name = Values(first_name), "
				+ "last_name = Values(last_name), "
				+ "photo_url = Values(photo_url), "
				+ "photo = Values(photo)";
		
		//Get image file type from full URL
		String photoUrl = legislator.getPhotoUrl();
		if ( photoUrl == null ) throw new IllegalArgumentException( "Legislator.getPhotoUrl() cannot be null." );
		String ext = photoUrl.substring( photoUrl.lastIndexOf(".")+1 ).toLowerCase();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//Cannot connect to database without a driver, so failure
			e.printStackTrace();
			return false;
		}
		
		try (	Connection conn = DriverManager.getConnection( getDatabaseConnectionURL(), getDatabaseUser(), getDatabasePassword() );
				PreparedStatement stmt = conn.prepareStatement( sql ); 
			) {
			
			stmt.setString( 1, legislator.getBioguide_id() );
			stmt.setString( 2, legislator.getFirst_name() );
			stmt.setString( 3, legislator.getLast_name() );
			stmt.setString( 4, photoUrl );
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(photo, ext, baos );
			ByteArrayInputStream is = new ByteArrayInputStream( baos.toByteArray() );
			
			stmt.setBinaryStream( 5, is );
			
			stmt.executeUpdate();
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Load the photo for the given legislator from the database.
	 * <br>
	 * This code will also update {@link Legislator#setPhotoUrl(String)} with data from the database.
	 * 
	 * @param legislator	The legislator whose photo to load
	 * @return				The photo, or null if none found
	 */
	public static BufferedImage loadLegislatorImage( Legislator legislator ) {
		
		String sql = "Select photo_url, photo from Congress where bioguide_id = ?";
		BufferedImage retVal = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			//Cannot connect to database without a driver, so act as if no image found
			e.printStackTrace();
			return null;
		}
		
		try (	Connection conn = DriverManager.getConnection( getDatabaseConnectionURL(), getDatabaseUser(), getDatabasePassword() );
				PreparedStatement stmt = conn.prepareStatement( sql ); 
			) {
			
			stmt.setString( 1, legislator.getBioguide_id() );
			
			ResultSet results = stmt.executeQuery();
	        while( results.next() ) {
	        	//Since bioguide_id is the primary key, there really should be only one result
	        	legislator.setPhotoUrl( results.getString( "photo_url" ) );
	        	Blob photoBlob = results.getBlob( "photo" );
	        	if ( photoBlob != null )
	        		retVal = ImageIO.read( photoBlob.getBinaryStream() );
	        }
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		
		//Update images
		//In case database was manually updated, this code downloads the photos from the given URL and caches them in the database
		if ( retVal == null && !StringUtils.isEmpty( legislator.getPhotoUrl() ) ) {
			//Load photo and store in database
			retVal = ( new PhotoDownloader() ).downloadPhotoUrl(legislator.getPhotoUrl(), legislator);
			//Update database
			Database.writeLegislatorToDatabase(legislator, retVal);
		}
		
		
		return retVal;
	}
	
	private static String getDatabaseUser() {
		return Configuration.getConfiguration().getProperty(DB_USER);
	}
	
	private static String getDatabasePassword() {
		return Configuration.getConfiguration().getProperty(DB_PASS);
	}
	
	private static String getDatabaseConnectionURL() {
		return Configuration.getConfiguration().getProperty(DB_CONN);
	}
	
}
