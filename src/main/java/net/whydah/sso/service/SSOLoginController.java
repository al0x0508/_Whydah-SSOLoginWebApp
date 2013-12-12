package net.whydah.sso.service;

import net.whydah.sso.service.config.AppConfig;
import net.whydah.sso.service.data.UserCredential;
import net.whydah.sso.service.data.UserNameAndPasswordCredential;
import net.whydah.sso.service.data.WhydahUserTokenId;
import net.whydah.sso.service.util.SSOHelper;
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

        WhydahUserTokenId usertokenId = ssoHelper.getTokenidFromCookie(request);
        if (usertokenId.isValid()) {
            // TODO:  must get ticketid if we want to add to redirectsecurely
            //redirectURI = ssoHelper.appendTokenIDToRedirectURI(redirectURI, usertokenId.getUsertokenid());
            logger.info("Redirecting to {}", redirectURI);
            return "action";
        }
        setEnabledLoginTypes(model);
        return "login";
    }



    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, Model model) {
        String userTicket = request.getParameter(SSOHelper.USERTICKET);
        if (userTicket != null && userTicket.length() < 3) {
            model.addAttribute("TicketID", userTicket);
        }
        String userTokenId = ssoHelper.getTokenidFromCookie(request).toString();
        if (userTokenId != null && userTokenId.length() > 3) {
            model.addAttribute("TokenID", userTokenId);
            model.addAttribute("Token", ssoHelper.getUserTokenByTokenID(userTicket));
        }
        return "welcome";
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserCredential user = new UserNameAndPasswordCredential(request.getParameter("user"), request.getParameter("password"));
        String redirectURI = getRedirectURI(request);
        logger.info("Found redirect:", redirectURI);
        model.addAttribute("logoURL", LOGOURL);
        String ticketID = UUID.randomUUID().toString();
        String userTokenXml = ssoHelper.getUserToken(user, ticketID);

        if (userTokenXml == null) {
            logger.info("getUserToken failed. Redirecting to login.");
            model.addAttribute("loginError", "Could not log in.");
            setEnabledLoginTypes(model);
            model.addAttribute("redirectURI", redirectURI);
            return "login";
        }
        response.addCookie(ssoHelper.createUserTokenCookie(userTokenXml));

        if (redirectURI.toLowerCase().contains("userticket")) {
            // Do not overwrite ticket
        } else {
            redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, ticketID);

        }
        logger.info("Redirecting to {}", redirectURI);
        model.addAttribute("redirectURI", redirectURI);
        return "action";
    }


    private void setEnabledLoginTypes(Model model) {
        model.addAttribute("signupEnabled", ssoHelper.getEnabledLoginTypes().isSignupEnabled());
        model.addAttribute("facebookLoginEnabled", ssoHelper.getEnabledLoginTypes().isFacebookLoginEnabled());
        model.addAttribute("openidLoginEnabled", ssoHelper.getEnabledLoginTypes().isOpenIdLoginEnabled());
        model.addAttribute("omniLoginEnabled", ssoHelper.getEnabledLoginTypes().isOmniLoginEnabled());
        model.addAttribute("netIQLoginEnabled", ssoHelper.getEnabledLoginTypes().isNetIQLoginEnabled());
        model.addAttribute("userpasswordLoginEnabled", ssoHelper.getEnabledLoginTypes().isUserpasswordLoginEnabled());

        if (ssoHelper.getEnabledLoginTypes().isNetIQLoginEnabled()) {
            setNetIQOverrides(model);
        }
    }

    private static void setNetIQOverrides(Model model) {
        try {
            model.addAttribute("netIQtext", AppConfig.readProperties().getProperty("logintype.netiq.text"));
            model.addAttribute("netIQimage", AppConfig.readProperties().getProperty("logintype.netiq.logo"));
        } catch (IOException ioe) {
            model.addAttribute("netIQtext", "NetIQ");
            model.addAttribute("netIQimage", "images/netiqlogo.png");
        }
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
