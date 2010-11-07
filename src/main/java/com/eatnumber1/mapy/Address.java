package com.eatnumber1.mapy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class Address {
	@Nullable
	private String type;

	@Nullable
	private String street;

	@Nullable
	private String city;

	@Nullable
	private String state;

	@Nullable
	private String zip;

	public Address( String type, String street, String city, String state, String zip ) {
		this.type = type;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Address");
		sb.append("{type='").append(type).append('\'');
		sb.append(", street='").append(street).append('\'');
		sb.append(", city='").append(city).append('\'');
		sb.append(", state='").append(state).append('\'');
		sb.append(", zip='").append(zip).append('\'');
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( !(o instanceof Address) ) return false;

		Address address = (Address) o;

		if( city != null ? !city.equals(address.city) : address.city != null ) return false;
		if( state != null ? !state.equals(address.state) : address.state != null ) return false;
		if( street != null ? !street.equals(address.street) : address.street != null ) return false;
		if( type != null ? !type.equals(address.type) : address.type != null ) return false;
		//noinspection RedundantIfStatement
		if( zip != null ? !zip.equals(address.zip) : address.zip != null ) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (street != null ? street.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (zip != null ? zip.hashCode() : 0);
		return result;
	}
}
