package net.whydah.sso;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.data.UserCredential;
import net.whydah.sso.data.UserNameAndPasswordCredential;
import net.whydah.sso.data.WhydahUserTokenId;
import net.whydah.sso.util.ModelHelper;
import net.whydah.sso.util.SSOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

@Controller
public class SSOLoginController {
    private final static Logger logger = LoggerFactory.getLogger(SSOLoginController.class);
    public static final String DEFAULT_REDIRECT = "welcome";
    private final int MIN_REDIRECT_SIZE=4;
    private final SSOHelper ssoHelper = new SSOHelper();
    private final ModelHelper modelHelper = new ModelHelper(this);
    private String LOGOURL = "/sso/images/site-logo.png";

    public SSOLoginController() throws IOException {
        Properties properties = AppConfig.readProperties();
        String MY_APP_URI = properties.getProperty("myuri");
        LOGOURL = properties.getProperty("logourl");
    }


    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String redirectURI = getRedirectURI(request);
        logger.trace("login - Found redirectURI: {}",redirectURI);
        model.addAttribute("logoURL", LOGOURL);
        model.addAttribute("redirectURI", redirectURI);

        WhydahUserTokenId usertokenId = ssoHelper.getUserTokenIdFromCookie(request);
        logger.trace("login - Found usertokenID from whydah cookie");
        if (usertokenId.isValid()) {
            logger.trace("login - Found usertokenID is Valid");

            if (DEFAULT_REDIRECT.equalsIgnoreCase(redirectURI)){
                logger.trace("login - Did not find any sensible redirect, using /welcome");
                model.addAttribute("redirect", redirectURI);
                logger.info("login - Redirecting to {}", redirectURI);
                return "action";

            }
            String userTicket = UUID.randomUUID().toString();
            if (ssoHelper.createTicketForUserTokenID(userTicket,usertokenId.toString())){
                redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, userTicket);

                // Action use redirect - not redirectURI
                model.addAttribute("redirect", redirectURI);
                logger.info("login - Redirecting to {}", redirectURI);
                return "action";

            }

        }
        ModelHelper.setEnabledLoginTypes(ssoHelper,model);
        return "login";
    }



    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, Model model) {
        String userTicket = request.getParameter(SSOHelper.USERTICKET);
        model.addAttribute("logoURL", LOGOURL);
        model.addAttribute("iammode", ApplicationMode.getApplicationMode());
        if (userTicket != null && userTicket.length() > 3) {
            logger.trace("welcome - Using userticket");
            model.addAttribute(SSOHelper.USERTICKET, userTicket);
            String userToken= ssoHelper.getUserTokenByUserTicket(userTicket);
            model.addAttribute(SSOHelper.USERTOKEN, userToken);
            model.addAttribute(SSOHelper.REALNAME, ssoHelper.getRealName(userToken));
            model.addAttribute(SSOHelper.USER_TOKEN_ID, ssoHelper.getUserTokenId(userToken) );
            return "welcome";
        }
        String userTokenId = ssoHelper.getUserTokenIdFromCookie(request).toString();
        if (userTokenId != null && userTokenId.length() > 3) {
            logger.trace("welcome - No userticket, using usertokenID from cookie");
            model.addAttribute(SSOHelper.USERTICKET, "No userticket, using usertokenID");
            model.addAttribute(SSOHelper.USER_TOKEN_ID, userTokenId);
            String userToken= ssoHelper.getUserTokenByUserTokenID(userTokenId);
            model.addAttribute(SSOHelper.REALNAME, ssoHelper.getRealName(userToken));
            model.addAttribute(SSOHelper.USERTOKEN,userToken );
            return "welcome";
        } else {
            throw new UnauthorizedException();
        }
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserCredential user = new UserNameAndPasswordCredential(request.getParameter("user"), request.getParameter("password"));
        String redirectURI = getRedirectURI(request);
        logger.info("action - Found redirect:", redirectURI);
        model.addAttribute("logoURL", LOGOURL);
        String userTicket = UUID.randomUUID().toString();
        String userTokenXml = ssoHelper.getUserToken(user, userTicket);

        if (userTokenXml == null) {
            logger.warn("action - getUserToken failed. Redirecting to login.");
            model.addAttribute("loginError", "Could not log in.");
            ModelHelper.setEnabledLoginTypes(ssoHelper,model);
            model.addAttribute("redirectURI", redirectURI);
            return "login";
        }

        response.addCookie(ssoHelper.createUserTokenCookie(userTokenXml));

        // ticket on redirect
        if (redirectURI.toLowerCase().contains("userticket")) {
            // Do not overwrite ticket
        } else {
            redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, userTicket);
        }
        // Action use redirect...
        model.addAttribute("redirect", redirectURI);
        model.addAttribute("redirectURI", redirectURI);
        logger.info("action - Redirecting to {}", redirectURI);
        return "action";
    }



    private String getRedirectURI(HttpServletRequest request) {
        String redirectURI = request.getParameter("redirectURI");
        logger.trace("getRedirectURI - redirectURI from request: {}", redirectURI);
        if (redirectURI == null || redirectURI.length() < 1) {
            logger.trace("getRedirectURI - No redirectURI found, setting to {}", DEFAULT_REDIRECT);
            return DEFAULT_REDIRECT;
        }
        return redirectURI;
    }

}


