package net.whydah.sso;

import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.util.SSOHelper;
import net.whydah.token.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertTrue;

/**
 * @author Stig@Lau.no
 * End-end tests using the web interface of Whydah SSO Login
 */
public class FunctionalWebTests {
    static Main tokenService;
    static ServerRunner ssoService;

    @BeforeClass
    public static void startServers() throws Exception {
        System.setProperty(ApplicationMode.IAM_MODE_KEY, ApplicationMode.DEV);
        ssoService = new ServerRunner();
        ssoService.start();
        tokenService = new Main() { { startServer();}};
    }

    @AfterClass
    public static void stopServers() throws Exception {
        ssoService.stop();
        tokenService.stop();
    }

    @Test
    public void testUnverifiedUserGivesHTTP401(){
        try {
            URL url = new URL(ServerRunner.ROOT_URL + "welcome?userticket=998-a475-4486-a6bf-898");
            url.openConnection().connect();
        } catch (IOException ioe) {
            assertTrue(ioe.getMessage().contains("Server returned HTTP response code: 401"));
        }
    }

    @Test
    @Ignore // Just a template for future tests
    public void cookiedTest(){
        URL url;
        URLConnection conn;
        try {
            url = new URL(ServerRunner.ROOT_URL + "welcome?userticket=998-a475-4486-a6bf-898");
            String myCookie = SSOHelper.USER_TOKEN_REFERENCE_NAME + "=igbrown";
            conn = url.openConnection();
            conn.setRequestProperty("Cookie", myCookie);
            conn.connect();
        } catch (IOException ioe) {
            assertTrue(ioe.getMessage().contains("Server returned HTTP response code: 401"));
        }
    }
}
