package com.eatnumber1.mapy;

import geo.google.GeoAddressStandardizer;
import geo.google.GeoException;
import geo.google.datamodel.GeoAddress;
import geo.google.datamodel.GeoUsAddress;
import org.jetbrains.annotations.NotNull;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class MemberDao {
	@NotNull
	private LdapTemplate ldapTemplate;

	@NotNull
	private GeoAddressStandardizer geocoder;

	public MemberDao( @NotNull LdapTemplate ldapTemplate, @NotNull GeoAddressStandardizer geocoder ) {
		this.ldapTemplate = ldapTemplate;
		this.geocoder = geocoder;
	}

	public MemberDao( String s, String s2 ) {
	}

	@SuppressWarnings({ "unchecked" })
	private List<LdapGeoUsAddress> getAddresses() {
		/*AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectClass", "address"));
		filter.and(new EqualsFilter("addressName", "Home"));*/
		return ldapTemplate.search("ou=Users", "(&(&(objectClass=address)(addressName=Home))(uid:dn:=russ))",
				SearchControls.SUBTREE_SCOPE, new String[] {
						"addressName",
						"addressStreet",
						"addressCity",
						"addressState",
						"addressZip"
				}, new AbstractContextMapper() {
					@Override
					protected Object doMapFromContext( DirContextOperations ctx ) {
						return new LdapGeoUsAddress(
								((DistinguishedName) ctx.getDn()).getLdapRdn(1).getValue(),
								ctx.getStringAttribute("addressStreet"),
								ctx.getStringAttribute("addressCity"),
								ctx.getStringAttribute("addressState"),
								ctx.getStringAttribute("addressZip")
						);
					}
				});
	}

	@SuppressWarnings({ "unchecked" })
	public Collection<Member> getPeople() throws GeoException {
		List<LdapGeoUsAddress> addressList = getAddresses();
		Map<String, GeoAddress> addresses = new HashMap<String, GeoAddress>(addressList.size());
		for( LdapGeoUsAddress addr : addressList ) {
			List<GeoAddress> geoAddresses = geocoder.standardizeToGeoAddresses(addr);
			if( geoAddresses.size() > 1 ) throw new GeoException("More than one address returned for " + addr);
			addresses.put(addr.getUid(), geoAddresses.get(0));
		}
		// Now addressList can be gc'ed before we get lots more data with the next lookup.
		//noinspection UnusedAssignment
		addressList = null;

		List<LdapMember> people = ldapTemplate.search("ou=Users", "(&(objectClass=houseMember)(uid=russ))",
				SearchControls.ONELEVEL_SCOPE, new String[] {
						"givenName",
						"sn",
						"uid"
				}, new AbstractContextMapper() {
					@Override
					protected Object doMapFromContext( DirContextOperations ctx ) {
						return new LdapMember(
								ctx.getStringAttribute("uid"),
								new Name(
										ctx.getStringAttribute("givenName"),
										ctx.getStringAttribute("sn")
								)
						);
					}
				}
		);
		List<Member> ret = new ArrayList<Member>(people.size());
		for( LdapMember p : people ) {
			p.setAddress(addresses.get(p.getUid()));
			ret.add(p.produce());
		}
		return Collections.unmodifiableList(ret);
	}

	private static class LdapMember extends Member {
		private GeoAddress address;

		private LdapMember( String uid, Name name ) {
			super(uid, name, null);
		}

		public void setAddress( GeoAddress address ) {
			this.address = address;
		}

		public Member produce() {
			return new Member(getUid(), getName(), address);
		}
	}

	private static class LdapGeoUsAddress extends GeoUsAddress {
		@NotNull
		private String uid;

		private LdapGeoUsAddress( @NotNull String uid, String street, String city, String state, String zip ) {
			this.uid = uid;
			setAddressLine1(street);
			setCity(city);
			setState(state);
			setPostalCode(zip);
		}

		@NotNull
		public String getUid() {
			return uid;
		}
	}
}
