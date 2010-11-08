package com.eatnumber1.mapdap;

import geo.google.GeoAddressStandardizer;
import geo.google.GeoException;
import geo.google.datamodel.GeoAddress;
import geo.google.datamodel.GeoAddressAccuracy;
import geo.google.datamodel.GeoStatusCode;
import geo.google.datamodel.GeoUsAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

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
	private static Log log = LogFactory.getLog(MemberDao.class);

	@NotNull
	private LdapTemplate ldapTemplate;

	@NotNull
	private GeoAddressStandardizer geocoder;

	public MemberDao( @NotNull LdapTemplate ldapTemplate, @NotNull GeoAddressStandardizer geocoder ) {
		this.ldapTemplate = ldapTemplate;
		this.geocoder = geocoder;
	}

	@SuppressWarnings({ "unchecked" })
	private List<LdapGeoUsAddress> getAddresses() {
		log.debug("Retrieving addresses from CSH LDAP");
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectClass", "address"));
		filter.and(new EqualsFilter("addressName", "Home"));
		/* "(&(&(objectClass=address)(addressName=Home))(uid:dn:=russ))" */
		return ldapTemplate.search("ou=Users", filter.encode(),
				SearchControls.SUBTREE_SCOPE, new String[] {
						"addressName",
						"addressStreet",
						"addressCity",
						"addressState",
						"addressZip"
				}, new AbstractContextMapper() {
					@Override
					protected Object doMapFromContext( DirContextOperations ctx ) {
						log.trace("Mapping context " + ctx + " to an LdapGeoUsAddress");
						// TODO: Hax
						String street = ctx.getStringAttribute("addressStreet");
						if( street != null && street.contains("#") ) street = street.replace("#", "Number ");
						return new LdapGeoUsAddress(
								((DistinguishedName) ctx.getDn()).getLdapRdn(1).getValue(),
								street,
								ctx.getStringAttribute("addressCity"),
								ctx.getStringAttribute("addressState"),
								ctx.getStringAttribute("addressZip")
						);
					}
				});
	}

	@SuppressWarnings({ "unchecked" })
	private List<LdapMember> _getMembers() {
		log.debug("Retrieving members from CSH LDAP");
		/* "(&(objectClass=houseMember)(uid=russ))" */
		return ldapTemplate.search("ou=Users", "(objectClass=houseMember)",
				SearchControls.ONELEVEL_SCOPE, new String[] {
						"givenName",
						"sn",
						"uid"
				}, new AbstractContextMapper() {
					@Override
					protected Object doMapFromContext( DirContextOperations ctx ) {
						log.trace("Mapping context " + ctx + " to a LdapMember");
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
	}

	private Map<String, GeoAddress> geocodeAddresses( List<LdapGeoUsAddress> addressList ) throws GeoException {
		Map<String, GeoAddress> addresses = new HashMap<String, GeoAddress>(addressList.size());
		log.debug("Geocoding addresses");
		for( LdapGeoUsAddress addr : addressList ) {
			// Shortcut out of the address is empty.
			if( addr.isEmpty() ) continue;
			String addressLine = addr.toAddressLine();
			log.trace("Geocoding address " + addressLine);
			try {
				List<GeoAddress> geoAddresses = geocoder.standardizeToGeoAddresses(addr);
				if( geoAddresses.size() > 1 ) {
					log.warn("More than one address returned for " + addr.toAddressLine());
					log.debug("Addresses are " + geoAddresses);
				} else {
					GeoAddress geoAddress = geoAddresses.get(0);
					if( GeoAddressAccuracy.UNKNOWN_LOCATION.equals(geoAddress.getAccuracy()) )
						log.warn("Geocoded address " + geoAddress + " is at an unknown location");
					addresses.put(addr.getUid(), geoAddress);
				}
			} catch( GeoException e ) {
				if( !GeoStatusCode.G_GEO_UNKNOWN_ADDRESS.equals(e.getStatus()) ) throw e;
				if( log.isTraceEnabled() ) {
					log.trace("Unable to geocode address " + addressLine, e);
				} else {
					log.warn("Unable to geocode address " + addressLine);
				}
			}
		}
		return addresses;
	}

	public Collection<Member> getMembers() throws GeoException {
		Map<String, GeoAddress> addresses = geocodeAddresses(getAddresses());
		List<LdapMember> members = _getMembers();
		List<Member> ret = new ArrayList<Member>(members.size());
		for( LdapMember p : members ) {
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
			if( street != null ) setAddressLine1(street);
			if( city != null ) setCity(city);
			if( state != null ) setState(state);
			if( zip != null ) setPostalCode(zip);
		}

		@NotNull
		public String getUid() {
			return uid;
		}

		public boolean isEmpty() {
			return "".equals(getAddressLine1()) && "".equals(getCity()) && "".equals(getState()) && "".equals(getPostalCode());
		}
	}
}
