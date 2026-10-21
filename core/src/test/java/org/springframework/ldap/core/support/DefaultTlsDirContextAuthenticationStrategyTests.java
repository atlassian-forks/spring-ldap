package org.springframework.ldap.core.support;

import org.junit.Test;
import org.springframework.ldap.AuthenticationException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultTlsDirContextAuthenticationStrategyTests {
	@Mock
	private LdapContext context;

	private DefaultTlsDirContextAuthenticationStrategy strategy = new DefaultTlsDirContextAuthenticationStrategy();

	// gh-430, gh-502
	@Test
	public void applyAuthenticationThenLookupInvoked() throws Exception {
		this.strategy.applyAuthentication(this.context, "username", "password");

		verify(this.context).lookup("");
	}

	@Test
	public void applyAuthenticationWhenUserDnIsSetAndPasswordIsEmptyThenAuthenticationException() {
		assertThatExceptionOfType(javax.naming.AuthenticationException.class)
			.isThrownBy(() -> this.strategy.applyAuthentication(this.context, "username", ""));
		verifyZeroInteractions(this.context);
	}

	@Test
	public void applyAuthenticationWhenUserDnIsSetAndPasswordIsNullThenAuthenticationException() {
		assertThatExceptionOfType(javax.naming.AuthenticationException.class)
			.isThrownBy(() -> this.strategy.applyAuthentication(this.context, "username", null));
		verifyZeroInteractions(this.context);
	}

    @Test
    public void getContextWhenTlsPasswordIsEmptyThenClosesContext() throws Exception {
        assertRejectedTlsContextIsClosed("");
    }

    @Test
    public void getContextWhenTlsPasswordIsNullThenClosesContext() throws Exception {
        assertRejectedTlsContextIsClosed(null);
    }

    @Test
    public void applyAuthenticationWhenAnonymousThenSucceeds() throws Exception {
        strategy.applyAuthentication(context, "", "");
        verify(context).addToEnvironment(Context.SECURITY_PRINCIPAL, "");
        verify(context).addToEnvironment(Context.SECURITY_CREDENTIALS, "");
        verify(context).lookup("");
    }

    @Test
    public void getContextWhenExternalTlsHasNoPasswordThenSucceeds() throws Exception {
        LdapContextSource source = tlsContextSource(new ExternalTlsDirContextAuthenticationStrategy());
        DirContext result = source.getContext("cn=certificate-user", "");
        assertThat(result).isSameAs(context);
        verify(context).addToEnvironment(Context.SECURITY_AUTHENTICATION, "EXTERNAL");
        verify(context, never()).close();
        result.close();
    }

    private void assertRejectedTlsContextIsClosed(String password) throws Exception {
        LdapContextSource source = tlsContextSource(strategy);
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> source.getContext("cn=user", password));
        verify(context).close();
        verify(context, never()).lookup("");
    }

    private LdapContextSource tlsContextSource(DirContextAuthenticationStrategy authenticationStrategy)
            throws Exception {
        StartTlsResponse response = mock(StartTlsResponse.class);
        when(context.extendedOperation(any(StartTlsRequest.class))).thenReturn(response);
        LdapContextSource source = new LdapContextSource() {
            @Override
            protected DirContext getDirContextInstance(Hashtable<String, Object> environment) {
                return context;
            }
        };
        source.setUrl("ldap://localhost:389");
        source.setUserDn("cn=service");
        source.setPassword("secret");
        source.setAuthenticationStrategy(authenticationStrategy);
        source.afterPropertiesSet();
        return source;
    }

}