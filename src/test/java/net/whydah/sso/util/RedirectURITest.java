package net.whydah.sso.util;

import net.whydah.sso.config.ApplicationMode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class RedirectURITest {

    private final UserTokenHandler userTokenHandler = new UserTokenHandler();

    @BeforeClass
    public static void setup() {
        System.setProperty(ApplicationMode.IAM_MODE_KEY, ApplicationMode.DEV);
    }


    @Test
    public void testRedirectLogic(){

        String redirectURI = "http://demo.getwhydah.com/test/hello";
        String userTicket = UUID.randomUUID().toString();
        // ticket on redirect
        if (redirectURI.toLowerCase().contains("userticket")) {
            // Do not overwrite ticket
        } else {
            redirectURI = userTokenHandler.appendTicketToRedirectURI(redirectURI, userTicket);

        }
        assertTrue(redirectURI.toLowerCase().contains("userticket"));
    }
}
