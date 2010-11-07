package com.eatnumber1.mapy;

import com.google.gdata.util.ServiceException;
import geo.google.GeoException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Mapy {
	public static void main( String[] args ) throws ServiceException, IOException, GeoException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("ldap.xml");

		CSHMapsService maps = (CSHMapsService) ctx.getBean("cshMapsService");
		CSHMapEntry map = maps.createMap();

		MemberDao dao = (MemberDao) ctx.getBean("memberDao");
		for( Member person : dao.getMembers() ) {
			if( person.getAddress() != null ) map.addMember(person);
		}
	}
}
