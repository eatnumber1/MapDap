package com.eatnumber1.mapy;

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
public class PersonDao {
	@NotNull
	private LdapTemplate ldapTemplate;

	public PersonDao( @NotNull LdapTemplate ldapTemplate ) {
		this.ldapTemplate = ldapTemplate;
	}

	@SuppressWarnings({ "unchecked" })
	private List<LdapAddress> getAddresses() {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectClass", "address"));
		filter.and(new EqualsFilter("addressName", "Home"));
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
						return new LdapAddress(
								((DistinguishedName) ctx.getDn()).getLdapRdn(1).getValue(),
								ctx.getStringAttribute("addressName"),
								ctx.getStringAttribute("addressStreet"),
								ctx.getStringAttribute("addressCity"),
								ctx.getStringAttribute("addressState"),
								ctx.getStringAttribute("addressZip")
						);
					}
				});
	}

	@SuppressWarnings({ "unchecked" })
	public Collection<Person> getPeople() {
		List<LdapAddress> addressList = getAddresses();
		Map<String, Address> addresses = new HashMap<String, Address>(addressList.size());
		for( LdapAddress addr : addressList ) addresses.put(addr.getUid(), addr);
		// Now addressList can be gc'ed before we get lots more data with the next lookup.
		//noinspection UnusedAssignment
		addressList = null;

		List<LdapPerson> people = ldapTemplate.search("ou=Users", "(objectClass=houseMember)",
				SearchControls.ONELEVEL_SCOPE, new String[] {
						"givenName",
						"sn",
						"uid"
				}, new AbstractContextMapper() {
					@Override
					protected Object doMapFromContext( DirContextOperations ctx ) {
						return new LdapPerson(
								ctx.getStringAttribute("uid"),
								new Name(
										ctx.getStringAttribute("givenName"),
										ctx.getStringAttribute("sn")
								)
						);
					}
				}
		);
		List<Person> ret = new ArrayList<Person>(people.size());
		for( LdapPerson p : people ) {
			p.setAddress(addresses.get(p.getUid()));
			ret.add(p.produce());
		}
		return Collections.unmodifiableList(ret);
	}

	private static class LdapPerson extends Person {
		private Address address;

		private LdapPerson( String uid, Name name ) {
			super(uid, name, null);
		}

		public void setAddress( Address address ) {
			this.address = address;
		}

		public Person produce() {
			return new Person(getUid(), getName(), address);
		}
	}

	private static class LdapAddress extends Address {
		@NotNull
		private String uid;

		private LdapAddress( @NotNull String uid, String type, String street, String city, String state, String zip ) {
			super(type, street, city, state, zip);
			this.uid = uid;
		}

		@NotNull
		public String getUid() {
			return uid;
		}
	}
}
