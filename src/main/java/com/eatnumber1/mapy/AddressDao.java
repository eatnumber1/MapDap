package com.eatnumber1.mapy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.util.List;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class AddressDao {
	private static Log log = LogFactory.getLog(AddressDao.class);

	private LdapTemplate ldapTemplate;

	public AddressDao( LdapTemplate ldapTemplate ) {
		this.ldapTemplate = ldapTemplate;
	}

	@SuppressWarnings({ "unchecked" })
	public List<Address> getAddresses() {
		return ldapTemplate.search("ou=Users", "(addressName=*)",
				SearchControls.SUBTREE_SCOPE, new String[] {
						"addressName",
						"addressStreet",
						"addressCity",
						"addressState",
						"addressZip"
				}, new AttributesMapper() {
			@Override
			public Object mapFromAttributes( Attributes attributes ) throws NamingException {
				Attribute type = attributes.get("addressName"),
						city = attributes.get("addressCity"),
						state = attributes.get("addressState"),
						zip = attributes.get("addressZip");
				Integer zipInt;
				try {
					zipInt = zip == null ? null : Integer.valueOf((String) zip.get());
				} catch( NumberFormatException e ) {
					log.warn("Invalid zip code " + zip.get());
					zipInt = null;
				}
				return new Address(
						type == null ? null : (String) type.get(),
						city == null ? null : (String) city.get(),
						state == null ? null : (String) state.get(),
						zipInt
				);
			}
		});
	}
}
