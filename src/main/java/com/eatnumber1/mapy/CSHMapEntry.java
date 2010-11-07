package com.eatnumber1.mapy;

import com.google.gdata.client.maps.MapsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.maps.FeatureEntry;
import com.google.gdata.data.maps.MapEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import geo.google.datamodel.GeoAddress;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

/**
 * @author Russell Harmon
 * @since Nov 6, 2010
 */
public class CSHMapEntry {
	private MapsService service;

	private MapEntry entry;

	public CSHMapEntry( MapsService service, MapEntry entry ) {
		this.service = service;
		this.entry = entry;
	}

	public void addMember( @NotNull Member member ) throws IOException, ServiceException {
		Name name = member.getName();
		GeoAddress address = member.getAddress();
		Kml kml = new Kml();
		Coordinate coordinates = new Coordinate(address.getCoordinate().getLongitude(), address.getCoordinate().getLatitude());
		kml.createAndSetPlacemark()
				.withName(name.toString())
				.withAddress(address.getAddressLine())
				.createAndSetPoint()
				.withCoordinates(Collections.singletonList(coordinates));
		System.out.println(member);
		StringWriter sw = new StringWriter();
		kml.marshal(sw);
		// Ugly hax
		sw.getBuffer().delete(0, sw.getBuffer().indexOf(">") + 1);
		sw.getBuffer().delete(0, sw.getBuffer().indexOf(">") + 1);
		sw.getBuffer().delete(sw.getBuffer().lastIndexOf("<"), sw.getBuffer().lastIndexOf(">") + 1);

		System.out.println(sw.toString());

		FeatureEntry feature = new FeatureEntry();
		XmlBlob blob = new XmlBlob();
		blob.setBlob(sw.toString());
		feature.setKml(blob);
		feature.setTitle(new PlainTextConstruct(name.toString()));
		service.insert(entry.getFeatureFeedUrl(), feature);
	}
}
