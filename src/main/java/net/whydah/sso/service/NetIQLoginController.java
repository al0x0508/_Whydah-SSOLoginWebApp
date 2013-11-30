package net.whydah.sso.service;

import com.restfb.types.User;
import net.whydah.sso.service.config.AppConfig;
import net.whydah.sso.service.data.FacebookUserCredential;
import net.whydah.sso.service.data.NetIQUserCredential;
import net.whydah.sso.service.data.UserCredential;
import net.whydah.sso.service.util.NetIQHelper;
import net.whydah.sso.service.util.SSOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Controller
public class NetIQLoginController {
        private static final Logger logger = LoggerFactory.getLogger(NetIQLoginController.class);
        private final SSOHelper ssoHelper = new SSOHelper();

        // set this to your servlet URL for the authentication servlet/filter
        private final String hetIQauthURI;

        public NetIQLoginController() throws IOException {
            Properties properties = AppConfig.readProperties();
            String MY_APP_URI = properties.getProperty("myuri");
            hetIQauthURI =  properties.getProperty("netIQauthURL");
        }


        @RequestMapping("/netiqlogin")
        public String netIQLogin(HttpServletRequest request, Model model) throws MalformedURLException {
            String LOGOURL="/sso/images/site-logo.png";
            try {
                Properties properties = AppConfig.readProperties();
                LOGOURL = properties.getProperty("logourl");

            } catch (Exception e){

            }
            model.addAttribute("logoURL", LOGOURL);

            model.addAttribute("redirect", hetIQauthURI);
            logger.info("Redirecting to {}", hetIQauthURI);
            return "action";
        }

        @RequestMapping("/netiqauth")
        public String netiqAuth(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {

            NetIQHelper helper = new NetIQHelper();
            Map.Entry<String, String> pair = helper.loginAndCreateNetIQUser(request);
            if (pair == null) {
                logger.error("Could not fetch netiq user.");
                //TODO Do we need to add client redirect URI here?
                return "login";
            }
            String netiqAccessToken = pair.getKey();
            String netIQUser = pair.getValue();

            UserCredential userCredential;
            try {
                userCredential = new NetIQUserCredential(netiqAccessToken, netIQUser);
            } catch(IllegalArgumentException iae) {
                logger.error(iae.getLocalizedMessage());
                //TODO Do we need to add client redirect URI here?
                return "login";
            }

            String ticket = UUID.randomUUID().toString();

            //Check om fbToken har session i lokal cache i TokenService
            // Hvis ja, hent whydah user token og legg ticket på model eller på returURL.
            String userTokenXml = ssoHelper.getUserToken(userCredential, ticket);

            if (userTokenXml == null) {
                logger.info("getUserToken failed. Try to create new user using facebook credentials.");
                // Hvis nei, hent brukerinfo fra FB, kall tokenService. med user credentials for ny bruker (lag tjenesten i TokenService).
                // Success etter ny bruker er laget = token. Alltid ticket id som skal sendes.

                String LOGOURL="/sso/images/site-logo.png";
                try {
                    Properties properties = AppConfig.readProperties();
                    LOGOURL = properties.getProperty("logourl");

                } catch (Exception e){

                }
                model.addAttribute("logoURL", LOGOURL);

                userTokenXml = ssoHelper.createAndLogonUser(netIQUser, netiqAccessToken, userCredential, ticket,request);
                if (userTokenXml == null) {
                    logger.error("createAndLogonUser failed. Redirecting to login page.");
                    String redirectURI = getRedirectURI(request);
                    model.addAttribute("redirectURI", redirectURI);
                    model.addAttribute("loginError", "Login error: Could not create or authenticate user.");
                    return "login";
                }
            }


            Cookie cookie = ssoHelper.createUserTokenCookie(userTokenXml);
            // cookie.setDomain("whydah.net");
            response.addCookie(cookie);

            String LOGOURL="/sso/images/site-logo.png";
            try {
                Properties properties = AppConfig.readProperties();
                LOGOURL = properties.getProperty("logourl");

            } catch (Exception e){

            }
            model.addAttribute("logoURL", LOGOURL);
            String clientRedirectURI = request.getParameter("state");
            clientRedirectURI = ssoHelper.appendTicketToRedirectURI(clientRedirectURI, ticket);
            logger.info("Redirecting to {}", clientRedirectURI);
            model.addAttribute("redirect", clientRedirectURI);
            return "action";
        }

        private String getRedirectURI(HttpServletRequest request) {
            String redirectURI = request.getParameter("fbauthURI");
            logger.debug("fbauthURI from request: {}", redirectURI);
            if (redirectURI == null || redirectURI.length() < 4) {
                logger.debug("No fbauthURI found, setting to {}", SSOLoginController.DEFAULT_REDIRECT);
                return SSOLoginController.DEFAULT_REDIRECT;
            }
            return redirectURI;
        }
    }