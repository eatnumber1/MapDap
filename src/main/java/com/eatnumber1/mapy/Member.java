package com.eatnumber1.mapy;

import geo.google.datamodel.GeoAddress;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class Member {
	private String uid;
	private Name name;

	@Nullable
	private GeoAddress address;

	public Member( String uid, Name name, GeoAddress address ) {
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

	@Nullable
	public GeoAddress getAddress() {
		return address;
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( !(o instanceof Member) ) return false;

		Member member = (Member) o;

		if( address != null ? !address.equals(member.address) : member.address != null ) return false;
		if( name != null ? !name.equals(member.name) : member.name != null ) return false;
		//noinspection RedundantIfStatement
		if( uid != null ? !uid.equals(member.uid) : member.uid != null ) return false;

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
		sb.append("Member");
		sb.append("{uid='").append(uid).append('\'');
		sb.append(", name=").append(name);
		sb.append(", address=").append(address.getAddressLine());
		sb.append('}');
		return sb.toString();
	}
}
