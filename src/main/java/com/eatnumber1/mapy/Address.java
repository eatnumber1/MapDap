package com.eatnumber1.mapy;

import com.sun.istack.internal.Nullable;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class Address {
	@Nullable
	private String type;

	@Nullable
	private String city;

	@Nullable
	private String state;

	@Nullable
	private Integer zip;

	public Address( String type, String city, String state, Integer zip ) {
		this.type = type;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Address");
		sb.append("{type='").append(type).append('\'');
		sb.append(", city='").append(city).append('\'');
		sb.append(", state='").append(state).append('\'');
		sb.append(", zip=").append(zip);
		sb.append('}');
		return sb.toString();
	}
}
