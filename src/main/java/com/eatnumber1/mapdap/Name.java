package com.eatnumber1.mapdap;

/**
 * @author Russell Harmon
 * @since Nov 6, 2010
 */
public class Name {
	private String first;
	private String last;

	public Name( String first, String last ) {
		this.first = first;
		this.last = last;
	}

	@Override
	public String toString() {
		return first + " " + last;
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( !(o instanceof Name) ) return false;

		Name name = (Name) o;

		if( first != null ? !first.equals(name.first) : name.first != null ) return false;
		//noinspection RedundantIfStatement
		if( last != null ? !last.equals(name.last) : name.last != null ) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = first != null ? first.hashCode() : 0;
		result = 31 * result + (last != null ? last.hashCode() : 0);
		return result;
	}
}
