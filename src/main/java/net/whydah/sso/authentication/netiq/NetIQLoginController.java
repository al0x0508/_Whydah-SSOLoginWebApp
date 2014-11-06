package net.whydah.sso.authentication.netiq;

import net.whydah.sso.authentication.ModelHelper;
import net.whydah.sso.authentication.UserCredential;
import net.whydah.sso.authentication.whydah.CookieManager;
import net.whydah.sso.config.AppConfig;
import net.whydah.sso.usertoken.UserTokenHandler;
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
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Controller
public class NetIQLoginController {
        private static final Logger logger = LoggerFactory.getLogger(NetIQLoginController.class);
        private final UserTokenHandler userTokenHandler = new UserTokenHandler();

        // set this to your servlet URL for the authentication servlet/filter
        private final String hetIQauthURI;
        String LOGOURL="/sso/images/site-logo.png";

        public NetIQLoginController() throws IOException {
            Properties properties = AppConfig.readProperties();
            String MY_APP_URI = properties.getProperty("myuri");
            hetIQauthURI =  properties.getProperty("netIQauthURL");
            LOGOURL=properties.getProperty("logourl");
        }


        @RequestMapping("/netiqlogin")
        public String netIQLogin(HttpServletRequest request, Model model) throws MalformedURLException {
            String clientRedirectURI = request.getParameter("redirectURI");
            model.addAttribute("logoURL", LOGOURL);

            model.addAttribute("redirect", hetIQauthURI+"?redirectURI="+clientRedirectURI);
            logger.info("Redirecting to {}", hetIQauthURI+"?redirectURI="+clientRedirectURI);
            return "action";
        }

        @RequestMapping("/netiqauth")
        public String netiqAuth(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {
            ModelHelper.setEnabledLoginTypes(userTokenHandler, model);

            model.addAttribute("logoURL", LOGOURL);


            try {
                model.addAttribute("netIQtext", AppConfig.readProperties().getProperty("logintype.netiq.text"));
                model.addAttribute("netIQimage", AppConfig.readProperties().getProperty("logintype.netiq.logo"));
            } catch (IOException ioe) {
                model.addAttribute("netIQtext", "NetIQ");
                model.addAttribute("netIQimage", "images/netiqlogo.png");
            }
            Enumeration headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                logger.trace("HTTP header - Name:{}  Header: {}",headerName,request.getHeader(headerName));
                if (!NetIQHelper.verifyNetIQHeader(headerName,request.getHeader(headerName))){
                    model.addAttribute("loginError", "Could not log in because NetIQ redirect verification failure.");

                    return "login";
                }
            }
            NetIQHelper helper = new NetIQHelper();
            logger.info(helper.getNetIQUserAsXml(request));
            Map.Entry<String, String> pair = helper.findNetIQUserFromRequest(request);
            if (pair == null) {
                logger.error("Could not find NetIQ user.");
                //TODO Do we need to add client redirect URI here?
                model.addAttribute("loginError", "Could not find NetIQ user.");
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
                model.addAttribute("loginError", "Illegal userdata from netIQ.");
                return "login";
            }

            String ticket = UUID.randomUUID().toString();

            //Check om fbToken har session i lokal cache i TokenService
            // Hvis ja, hent whydah user usertoken og legg ticket på model eller på returURL.
            String userTokenXml = userTokenHandler.getUserToken(userCredential, ticket);

            logger.debug("NetIQ respsonse:" + userTokenXml);
            if (userTokenXml == null) {
                logger.info("getUserToken failed. Try to create new user using netiq credentials.");
                // Hvis nei, hent brukerinfo fra FB, kall tokenService. med user credentials for ny bruker (lag tjenesten i TokenService).
                // Success etter ny bruker er laget = usertoken. Alltid ticket id som skal sendes.


                userTokenXml = userTokenHandler.createAndLogonUser(netIQUser, netiqAccessToken, userCredential, ticket,request);
                if (userTokenXml == null) {
                    logger.error("createAndLogonUser failed. Redirecting to login page.");
                    String redirectURI = request.getParameter("redirectURI");
                    model.addAttribute("redirectURI", redirectURI);
                    model.addAttribute("loginError", "Login error: Could not create or authenticate user.");
                    ModelHelper.setEnabledLoginTypes(userTokenHandler,model);
                    return "login";
                }
            }


            Cookie cookie = CookieManager.createUserTokenCookie(userTokenXml);
            response.addCookie(cookie);

            String clientRedirectURI = request.getParameter("redirectURI");
            if (clientRedirectURI!=null) {
                clientRedirectURI = userTokenHandler.appendTicketToRedirectURI(clientRedirectURI, ticket);
                logger.info("Redirecting to {}", clientRedirectURI);
                model.addAttribute("redirect", clientRedirectURI);
            }
            return "action";
        }

    }