package com.eatnumber1.mapy;

import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class Person {
	private String uid;
	private Name name;
	private Address address;

	public Person( String uid, Name name, Address address ) {
		this.uid = uid;
		this.name = name;
		this.address = address;
	}

	public String getUid() {
		return uid;
	}

	public Name getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( !(o instanceof Person) ) return false;

		Person person = (Person) o;

		if( address != null ? !address.equals(person.address) : person.address != null ) return false;
		if( name != null ? !name.equals(person.name) : person.name != null ) return false;
		//noinspection RedundantIfStatement
		if( uid != null ? !uid.equals(person.uid) : person.uid != null ) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = uid != null ? uid.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (address != null ? address.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Person");
		sb.append("{uid='").append(uid).append('\'');
		sb.append(", name=").append(name);
		sb.append(", address=").append(address);
		sb.append('}');
		return sb.toString();
	}
}
