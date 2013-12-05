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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.UUID;

@Controller
public class SSOLoginController {
    private final static Logger logger = LoggerFactory.getLogger(SSOLoginController.class);
    public static final String DEFAULT_REDIRECT = "welcome";
    private final SSOHelper ssoHelper = new SSOHelper();

    /**
     * Controlling the sign on page.
     * @param request Http-request
     * @param model data to be used in the template
     * @return template to display
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String redirectURI = getRedirectURI(request);
        String LOGOURL="/sso/images/site-logo.png";
        try {
            Properties properties = AppConfig.readProperties();
            LOGOURL = properties.getProperty("logourl");

        } catch (Exception e){

        }
        model.addAttribute("logoURL", LOGOURL);

        WhydahUserTokenId usertokenId = ssoHelper.getTokenidFromCookie(request);
        if (usertokenId.isValid()) {
            // TODO:  must get ticketid if we want to add to redirectsecurely
            //redirectURI = ssoHelper.appendTokenIDToRedirectURI(redirectURI, usertokenId.getUsertokenid());
            model.addAttribute("redirect", redirectURI);
            logger.info("Redirecting to {}", redirectURI);
            return "action";
        }
        
        model.addAttribute("redirectURI", redirectURI);
        setEnabledLoginTypes(model);
        
        return "login";
    }

	private void setEnabledLoginTypes(Model model) {
		model.addAttribute("facebookLoginEnabled", ssoHelper.getEnabledLoginTypes().isFacebookLoginEnabled());
        model.addAttribute("openidLoginEnabled", ssoHelper.getEnabledLoginTypes().isOpenIdLoginEnabled());
        model.addAttribute("omniLoginEnabled", ssoHelper.getEnabledLoginTypes().isOmniLoginEnabled());
        model.addAttribute("netIQLoginEnabled", ssoHelper.getEnabledLoginTypes().isNetIQLoginEnabled());
        model.addAttribute("userpasswordLoginEnabled", ssoHelper.getEnabledLoginTypes().isUserpasswordLoginEnabled());
	}

    private String getRedirectURI(HttpServletRequest request) {
        String redirectURI = request.getParameter("redirectURI");
        logger.debug("redirectURI from request: {}", redirectURI);
        if (redirectURI == null || redirectURI.length() < 4) {
            logger.debug("No redirectURI found, setting to {}", DEFAULT_REDIRECT);
            return DEFAULT_REDIRECT;
        }
        return redirectURI;
    }



    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, Model model) {
        String userTicket = request.getParameter(SSOHelper.USERTICKET);
        if (userTicket != null && userTicket.length() > 3) {
            model.addAttribute("TicketID", userTicket);
            //model.addAttribute("Token", ssoHelper.getUserTokenByTicket(userticket));
        }

        String userTokenId = request.getParameter(SSOHelper.USER_TOKEN_ID);
        if (userTokenId != null && userTokenId.length() > 3) {
            model.addAttribute("TokenID", userTokenId);
        }

        return "welcome";
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserCredential user = new UserNameAndPasswordCredential(request.getParameter("user"), request.getParameter("password"));

        String LOGOURL="/sso/images/site-logo.png";
        try {
            Properties properties = AppConfig.readProperties();
            LOGOURL = properties.getProperty("logourl");

        } catch (Exception e){

        }
        model.addAttribute("logoURL", LOGOURL);

        String redirectURI = getRedirectURI(request);
        logger.info("Found redirect:", redirectURI);

        String ticketID = UUID.randomUUID().toString();
        String userTokenXml = ssoHelper.getUserToken(user, ticketID);

        if (userTokenXml == null) {
            logger.info("getUserToken failed. Redirecting to login.");
            model.addAttribute("redirectURI", redirectURI);
            model.addAttribute("loginError", "Could not log in.");
            setEnabledLoginTypes(model);
            return "login";
        }


        Cookie cookie = ssoHelper.createUserTokenCookie(userTokenXml);
        response.addCookie(cookie);

        if (redirectURI.toLowerCase().contains("userticket")){
            // Do not overwrite ticket
        } else {
            redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, ticketID);

        }
        model.addAttribute("redirect", redirectURI);
        logger.info("Redirecting to {}", redirectURI);

        return "action";
    }
}
