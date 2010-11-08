package com.eatnumber1.mapdap;

import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class Address {
	@Nullable
	private String street;

	@Nullable
	private String city;

	@Nullable
	private String state;

	@Nullable
	private String zip;

	public Address( String street, String city, String state, String zip ) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	@Nullable
	public String getStreet() {
		return street;
	}

	@Nullable
	public String getCity() {
		return city;
	}

	@Nullable
	public String getState() {
		return state;
	}

	@Nullable
	public String getZip() {
		return zip;
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( !(o instanceof Address) ) return false;

		Address address = (Address) o;

		if( city != null ? !city.equals(address.city) : address.city != null ) return false;
		if( state != null ? !state.equals(address.state) : address.state != null ) return false;
		if( street != null ? !street.equals(address.street) : address.street != null ) return false;
		//noinspection RedundantIfStatement
		if( zip != null ? !zip.equals(address.zip) : address.zip != null ) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = street != null ? street.hashCode() : 0;
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (zip != null ? zip.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if( street != null ) sb.append(street).append(' ');
		if( city != null ) sb.append(city).append(' ');
		if( state != null ) sb.append(state).append(' ');
		if( zip != null ) sb.append(zip).append(' ');
		return sb.toString();
	}
}
