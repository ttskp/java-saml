package com.onelogin.saml2.test.authn;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;

import com.onelogin.saml2.authn.AuthnRequest;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import com.onelogin.saml2.util.Util;

@PrepareForTest({AuthnRequest.class})
public class AuthnRequestTest {

	 @Rule
	 public PowerMockRule rule = new PowerMockRule();

	/**
	 * Tests the getEncodedAuthnRequest method of AuthnRequest
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest#getEncodedAuthnRequest
	 */
	@Test
	public void testGetEncodedAuthnRequestSimulated() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		String authnRequestString = Util.getFileAsString("data/requests/authn_request.xml");
		AuthnRequest authnRequest = PowerMockito.spy(new AuthnRequest(settings));

		when(authnRequest, method(AuthnRequest.class, "getAuthnRequestXml")).withNoArguments().thenReturn(
				authnRequestString);

		String expectedAuthnRequestStringBase64 = Util.getFileAsString("data/requests/authn_request.xml.deflated.base64");
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();

		assertEquals(authnRequestStringBase64, expectedAuthnRequestStringBase64);
	}

	/**
	 * Tests the getEncodedAuthnRequest method of AuthnRequest
	 * Case: Only settings provided.
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest#getEncodedAuthnRequest
	 */
	@Test
	public void testGetEncodedAuthnRequestOnlySettings() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();
		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("ProviderName=\"SP Java Example\"")));
		
		settings = new SettingsBuilder().fromFile("config/config.all.properties").build();
		authnRequest = new AuthnRequest(settings);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("ProviderName=\"SP Java Example\""));
	}

	/**
	 * Tests the AuthnRequest Constructor
	 * The creation of a deflated SAML Request with the different values of ForceAuthn
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest
	 */
	@Test
	public void testForceAuthN() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("ForceAuthn=\"true\"")));

		authnRequest = new AuthnRequest(settings, false, false, false);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);		
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("ForceAuthn=\"true\"")));		

		authnRequest = new AuthnRequest(settings, true, false, false);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);		
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("ForceAuthn=\"true\""));
	}

	/**
	 * Tests the AuthnRequest Constructor
	 * The creation of a deflated SAML Request with the different values of IsPassive
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest
	 */
	@Test
	public void testIsPassive() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("IsPassive=\"true\"")));

		authnRequest = new AuthnRequest(settings, false, false, false);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);		
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("IsPassive=\"true\"")));		

		authnRequest = new AuthnRequest(settings, false, true, false);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);		
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("IsPassive=\"true\""));
	}

	/**
	 * Tests the AuthnRequest Constructor
	 * The creation of a deflated SAML Request with and without NameIDPolicy
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest
	 */
	@Test
	public void testNameIDPolicy() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("<samlp:NameIDPolicy"));
		assertThat(authnRequestStr, containsString("Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified\""));

		authnRequest = new AuthnRequest(settings, false, false, false);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);		
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("<samlp:NameIDPolicy")));		

		authnRequest = new AuthnRequest(settings, false, false, true);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);		
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("<samlp:NameIDPolicy"));
		assertThat(authnRequestStr, containsString("Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified\""));
	}

	/**
	 * Tests the AuthnRequest Constructor
	 * The creation of a deflated SAML Request with NameIDPolicy Encrypted
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest
	 */
	@Test
	public void testCreateEncPolicySAMLRequest() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.all.properties").build();

		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("<samlp:NameIDPolicy"));
		assertThat(authnRequestStr, containsString("Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:encrypted\""));
	}

	/**
	 * Tests the AuthnRequest Constructor
	 * The creation of a deflated SAML Request with and without AuthNContext
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest
	 */
	@Test
	public void testAuthNContext() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		List<String> requestedAuthnContext = new ArrayList<String>();
		settings.setRequestedAuthnContext(requestedAuthnContext);

		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("<samlp:RequestedAuthnContext")));

		requestedAuthnContext.add("urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
		settings.setRequestedAuthnContext(requestedAuthnContext);
		authnRequest = new AuthnRequest(settings);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("<samlp:RequestedAuthnContext Comparison=\"exact\">"));
		assertThat(authnRequestStr, containsString("<saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml:AuthnContextClassRef>"));

		requestedAuthnContext.add("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");
		settings.setRequestedAuthnContext(requestedAuthnContext);
		settings.setRequestedAuthnContext(requestedAuthnContext);
		authnRequest = new AuthnRequest(settings);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("<samlp:RequestedAuthnContext Comparison=\"exact\">"));
		assertThat(authnRequestStr, containsString("<saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml:AuthnContextClassRef>"));
		assertThat(authnRequestStr, containsString("<saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:X509</saml:AuthnContextClassRef>"));
	}

	@Test
	public void testAuthNId() throws Exception
	{
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		AuthnRequest authnRequest = new AuthnRequest(settings);
		final String authnRequestStr = Util.base64decodedInflated(authnRequest.getEncodedAuthnRequest());

		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("ID=\"" + authnRequest.getId() + "\""));
	}

	/**
	 * Tests the AuthnRequest Constructor
	 * The creation of a deflated SAML Request with and without Destination
	 *
	 * @throws Exception
	 * 
	 * @see com.onelogin.saml2.authn.AuthnRequest
	 */
	@Test
	public void testAuthNDestination() throws Exception {
		Saml2Settings settings = new SettingsBuilder().fromFile("config/config.min.properties").build();

		AuthnRequest authnRequest = new AuthnRequest(settings);
		String authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		String authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, containsString("Destination=\"http://idp.example.com/simplesaml/saml2/idp/SSOService.php\""));

		settings = new Saml2Settings();
		authnRequest = new AuthnRequest(settings);
		authnRequestStringBase64 = authnRequest.getEncodedAuthnRequest();
		authnRequestStr = Util.base64decodedInflated(authnRequestStringBase64);
		assertThat(authnRequestStr, containsString("<samlp:AuthnRequest"));
		assertThat(authnRequestStr, not(containsString("Destination=\"http://idp.example.com/simplesaml/saml2/idp/SSOService.php\"")));
	}
}