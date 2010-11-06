package com.eatnumber1.mapy;

import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy;

import javax.naming.directory.DirContext;
import java.util.Hashtable;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class CSHDirContextAuthenticationStrategy implements DirContextAuthenticationStrategy {
	private SimpleDirContextAuthenticationStrategy delegate = new SimpleDirContextAuthenticationStrategy();

	@SuppressWarnings({ "unchecked" })
	@Override
	public void setupEnvironment( Hashtable env, String userDn, String password ) {
		delegate.setupEnvironment(env, userDn, password);
		env.put("java.naming.ldap.factory.socket", CSHSocketFactory.class.getCanonicalName());
	}

	@Override
	public DirContext processContextAfterCreation( DirContext ctx, String userDn, String password ) {
		return delegate.processContextAfterCreation(ctx, userDn, password);
	}
}
