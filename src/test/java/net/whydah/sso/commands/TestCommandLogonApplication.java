package net.whydah.sso.commands;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.application.ApplicationCredential;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Observable;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by totto on 12/2/14.
 */
public class TestCommandLogonApplication {

    private static Properties properties;
    private static URI tokenServiceUri;
    private static ApplicationCredential applicationCredential;


    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty(ApplicationMode.IAM_MODE_KEY, ApplicationMode.TEST);
        properties = AppConfig.readProperties();
        tokenServiceUri = UriBuilder.fromUri(properties.getProperty("securitytokenservice")).build();
        applicationCredential = new ApplicationCredential();
        applicationCredential.setApplicationID(properties.getProperty("applicationid"));

    }


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        applicationCredential.setApplicationSecret("false secret");

        String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, applicationCredential).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertEquals("FallbackApplicationTokenID", myApplicationTokenID);

        Future<String> fAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationCredential).queue();
        assertEquals("FallbackApplicationTokenID", fAppTokenID.get());


        Observable<String> oAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationCredential).observe();
        // blocking
        assertEquals("FallbackApplicationTokenID", oAppTokenID.toBlocking().single());
    }

    @Test
    public void testApplicationLoginCommand() throws Exception {

        applicationCredential.setApplicationSecret(properties.getProperty("applicationsecret"));

        String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, applicationCredential).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertTrue(myApplicationTokenID.length() > 6);

        Future<String> fAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationCredential).queue();
        assertTrue(fAppTokenID.get().length() > 6);

        Observable<String> oAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationCredential).observe();
        // blocking
        assertTrue(oAppTokenID.toBlocking().single().length() > 6);
    }

}
