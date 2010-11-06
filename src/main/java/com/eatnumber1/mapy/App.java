package com.eatnumber1.mapy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main( String[] args ) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("ldap.xml");
		AddressDao dao = (AddressDao) ctx.getBean("addressDao");
		for( Address address : dao.getAddresses() ) {
			System.out.println(address);
		}
	}
}
