package net.whydah.sso.notinuse.commands;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
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
    private static String applicationid;


    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty(ApplicationMode.IAM_MODE_KEY, ApplicationMode.TEST);
        properties = AppConfig.readProperties();
        tokenServiceUri = UriBuilder.fromUri(properties.getProperty("securitytokenservice")).build();
        applicationid = properties.getProperty("applicationid");

    }


    @Test
    public void testApplicationLoginCommandFallback() throws Exception {

        String applicationsecret = "false secret";

        String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertEquals("FallbackApplicationTokenID", myApplicationTokenID);

        Future<String> fAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).queue();
        assertEquals("FallbackApplicationTokenID", fAppTokenID.get());


        Observable<String> oAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).observe();
        // blocking
        assertEquals("FallbackApplicationTokenID", oAppTokenID.toBlocking().single());
    }

    @Test
    public void testApplicationLoginCommand() throws Exception {

        String applicationsecret = properties.getProperty("applicationsecret");
        String myApplicationTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).execute();
        // System.out.println("ApplicationTokenID=" + myApplicationTokenID);
        assertTrue(myApplicationTokenID.length() > 6);

        Future<String> fAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).queue();
        assertTrue(fAppTokenID.get().length() > 6);

        Observable<String> oAppTokenID = new CommandLogonApplication(tokenServiceUri, applicationid, applicationsecret).observe();
        // blocking
        assertTrue(oAppTokenID.toBlocking().single().length() > 6);
    }

}
