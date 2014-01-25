package com.congressionalphotodirectory.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	private static Properties config;
	private static final String CONFIGURATION_FILENAME = "configuration.properties";
	
	private Configuration() {
		
	}
	
	public static Properties getConfiguration() {
		
		if ( config == null )
		{
			loadConfiguration();
		}
		
		return config;
	}

	private static void loadConfiguration() {
		
		config = new Properties();
		InputStream inStream = Configuration.class.getClassLoader().getResourceAsStream( CONFIGURATION_FILENAME );
		
		try {
			config.load( inStream );
		} catch (IOException e) {
			throw new RuntimeException( "Unable to load configuration", e );
		} finally {
			if ( inStream != null )
			{
				try { inStream.close(); } catch ( IOException e) {}
			}
		}
	}
}
