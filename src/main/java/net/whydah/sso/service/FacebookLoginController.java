package net.whydah.sso.service;

import com.restfb.types.User;
import net.whydah.sso.service.config.AppConfig;
import net.whydah.sso.service.data.FacebookUserCredential;
import net.whydah.sso.service.data.UserCredential;
import net.whydah.sso.service.util.FacebookHelper;
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

/**
 * @author <a href="mailto:erik@freecode.no">Erik Drolshammer</a>
 * @since 26/09/12
 */
@Controller
public class FacebookLoginController {
    private static final Logger logger = LoggerFactory.getLogger(FacebookLoginController.class);
    private final SSOHelper ssoHelper = new SSOHelper();

    // set this to your servlet URL for the authentication servlet/filter
    private final String fbauthURI;

    public FacebookLoginController() throws IOException {
        Properties properties = AppConfig.readProperties();
        String MY_APP_URI = properties.getProperty("myuri");
        fbauthURI = MY_APP_URI + "fbauth";
    }


    @RequestMapping("/fblogin")
    public String facebookLogin(HttpServletRequest request, Model model) throws MalformedURLException {
        String clientRedirectURI = request.getParameter("redirectURI");
        String facebookLoginUrl = FacebookHelper.getFacebookLoginUrl(clientRedirectURI, fbauthURI);

        model.addAttribute("redirect", facebookLoginUrl);
        logger.info("Redirecting to {}", facebookLoginUrl);
        return "action";
    }

    @RequestMapping("/fbauth")
    public String facebookAuth(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {
        String code = request.getParameter("code");

        Map.Entry<String, User> pair = FacebookHelper.loginAndCreateFacebookUser(code, fbauthURI);
        String fbAccessToken = pair.getKey();
        User fbUser = pair.getValue();

        if (pair == null) {
            logger.error("Could not fetch facebok user.");
            //TODO Do we need to add client redirect URI here?
            return "login";
        }
        UserCredential userCredential;
        try {
            userCredential = new FacebookUserCredential(fbUser.getId(), fbUser.getUsername());
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


            userTokenXml = ssoHelper.createAndLogonUser(fbUser, fbAccessToken, userCredential, ticket);
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