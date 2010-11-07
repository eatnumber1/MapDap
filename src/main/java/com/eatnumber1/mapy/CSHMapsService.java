package com.eatnumber1.mapy;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.data.maps.MapFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Russell Harmon
 * @since Nov 6, 2010
 */
public class CSHMapsService {
	private MapsService service;

	public CSHMapsService( MapsService service ) {
		this.service = service;
	}

	public CSHMapEntry createMap() throws IOException, ServiceException {
		URL mapUrl;
		try {
			URL feedUrl = new URL("http://maps.google.com/maps/feeds/maps/default/full");
			MapFeed resultFeed = service.getFeed(feedUrl, MapFeed.class);
			mapUrl = new URL(resultFeed.getEntryPostLink().getHref());
		} catch( MalformedURLException e ) {
			throw new RuntimeException(e);
		}

		// Create a MapEntry object
		MapEntry myEntry = new MapEntry();
		myEntry.setTitle(new PlainTextConstruct("CSH Members"));
		myEntry.setSummary(new PlainTextConstruct("CSH member's home addresses."));
		Person author = new Person("Russell Harmon", null, "russ@csh.rit.edu");
		myEntry.getAuthors().add(author);

		MapEntry entry = service.insert(mapUrl, myEntry);

		return new CSHMapEntry(service, entry);
	}
}
