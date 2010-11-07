package com.eatnumber1.mapy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Mapy {
	public static void main( String[] args ) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("ldap.xml");
		PersonDao dao = (PersonDao) ctx.getBean("personDao");
		for( Person person : dao.getPeople() ) System.out.println(person);
	}
}
