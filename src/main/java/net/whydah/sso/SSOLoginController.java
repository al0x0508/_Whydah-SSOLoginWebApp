package net.whydah.sso;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.data.UserCredential;
import net.whydah.sso.data.UserNameAndPasswordCredential;
import net.whydah.sso.data.WhydahUserTokenId;
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
        model.addAttribute("logoURL", LOGOURL);
        model.addAttribute("redirectURI", redirectURI);

        WhydahUserTokenId usertokenId = ssoHelper.getUserTokenIdFromCookie(request);
        if (usertokenId.isValid()) {
            // TODO:  must get ticketid if we want to add to redirectsecurely
            //redirectURI = ssoHelper.appendTokenIDToRedirectURI(redirectURI, usertokenId.getUsertokenid());

            // Action use redirect - not redirectURI
            model.addAttribute("redirect", redirectURI);
            logger.info("Redirecting to {}", redirectURI);
            return "action";
        }
        ModelHelper.setEnabledLoginTypes(ssoHelper,model);
        return "login";
    }



    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, Model model) {
        String userTicket = request.getParameter(SSOHelper.USERTICKET);
        if (userTicket != null && userTicket.length() < 3) {
            model.addAttribute(SSOHelper.USERTICKET, userTicket);
        }
        String userTokenId = ssoHelper.getUserTokenIdFromCookie(request).toString();
        if (userTokenId != null && userTokenId.length() > 3) {
            model.addAttribute(SSOHelper.USERTICKET, userTokenId);
            model.addAttribute(SSOHelper.USERTOKEN, ssoHelper.getUserTokenByTokenID(userTicket));
            return "welcome";
        } else {
            throw new UnauthorizedException();
        }
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserCredential user = new UserNameAndPasswordCredential(request.getParameter("user"), request.getParameter("password"));
        String redirectURI = getRedirectURI(request);
        logger.info("Found redirect:", redirectURI);
        model.addAttribute("logoURL", LOGOURL);
        String userTicket = UUID.randomUUID().toString();
        String userTokenXml = ssoHelper.getUserToken(user, userTicket);

        if (userTokenXml == null) {
            logger.warn("getUserToken failed. Redirecting to login.");
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
        logger.info("Redirecting to {}", redirectURI);
        return "action";
    }



    private String getRedirectURI(HttpServletRequest request) {
        String redirectURI = request.getParameter("redirectURI");
        logger.debug("redirectURI from request: {}", redirectURI);
        if (redirectURI == null || redirectURI.length() < 1) {
            logger.debug("No redirectURI found, setting to {}", DEFAULT_REDIRECT);
            return DEFAULT_REDIRECT;
        }
        return redirectURI;
    }

}


