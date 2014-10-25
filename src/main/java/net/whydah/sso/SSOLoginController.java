package net.whydah.sso;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.data.UserCredential;
import net.whydah.sso.data.UserNameAndPasswordCredential;
import net.whydah.sso.data.WhydahUserTokenId;
import net.whydah.sso.util.ModelHelper;
import net.whydah.sso.util.SSOHelper;
import net.whydah.sso.util.SessionHelper;
import net.whydah.sso.util.XpathHelper;
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
    public String login(HttpServletRequest request, HttpServletResponse response,Model model) {
        String redirectURI = getRedirectURI(request);
        logger.trace("login - Found redirectURI: {}", redirectURI);
        model.addAttribute(SessionHelper.LOGO_URL, LOGOURL);
        model.addAttribute(SessionHelper.REDIRECT_URI, redirectURI);

        WhydahUserTokenId usertokenId = ssoHelper.getUserTokenIdFromCookie(request, response);
        logger.trace("login - Found usertokenid={} from whydah cookie", usertokenId);
        if (usertokenId.isValid()) {
            logger.trace("login - Found usertokenid={} is valid", usertokenId);

            if (DEFAULT_REDIRECT.equalsIgnoreCase(redirectURI)){
                logger.trace("login - Did not find any sensible redirectURI, using /welcome");
                model.addAttribute(SessionHelper.REDIRECT, redirectURI);
                logger.info("login - Redirecting to {}", redirectURI);
                return "action";

            }
            String userTicket = UUID.randomUUID().toString();
            if (ssoHelper.createTicketForUserTokenID(userTicket, usertokenId.toString())){
                logger.info("login - created new userticket={} for usertokenid={}",userTicket, usertokenId);
                redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, userTicket);

                // Action use redirect - not redirectURI
                model.addAttribute(SessionHelper.REDIRECT, redirectURI);
                logger.info("login - Redirecting to {}", redirectURI);
                return "action";
            }

        }
        ModelHelper.setEnabledLoginTypes(ssoHelper,model);
        return "login";
    }



    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, HttpServletResponse response,Model model) {
        String userTicket = request.getParameter(SSOHelper.USERTICKET);
        model.addAttribute(SessionHelper.LOGO_URL, LOGOURL);
        model.addAttribute(SessionHelper.IAM_MODE, ApplicationMode.getApplicationMode());
        if (userTicket != null && userTicket.length() > 3) {
            logger.trace("welcome - Using userticket");
            model.addAttribute(SSOHelper.USERTICKET, userTicket);
            String userToken= ssoHelper.getUserTokenByUserTicket(userTicket);
            model.addAttribute(SSOHelper.USERTOKEN, userToken);
            model.addAttribute(SSOHelper.REALNAME, XpathHelper.getRealName(userToken));
            model.addAttribute(SSOHelper.USER_TOKEN_ID, XpathHelper.getUserTokenId(userToken) );
            return "welcome";
        }
        String userTokenId = ssoHelper.getUserTokenIdFromCookie(request,response).toString();
        if (userTokenId != null && userTokenId.length() > 3) {
            logger.trace("welcome - No userticket, using usertokenID from cookie");
            model.addAttribute(SSOHelper.USERTICKET, "No userticket, using usertokenID");
            model.addAttribute(SSOHelper.USER_TOKEN_ID, userTokenId);
            String userToken= ssoHelper.getUserTokenByUserTokenID(userTokenId);
            model.addAttribute(SSOHelper.REALNAME, XpathHelper.getRealName(userToken));
            model.addAttribute(SSOHelper.USERTOKEN,userToken );
            return "welcome";
        } else {
            throw new UnauthorizedException();
        }
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserCredential user = new UserNameAndPasswordCredential(request.getParameter(SessionHelper.USER), request.getParameter(SessionHelper.PASSWORD));
        String redirectURI = getRedirectURI(request);
        logger.info("action - Found redirect:", redirectURI);
        model.addAttribute(SessionHelper.LOGO_URL, LOGOURL);
        String userTicket = UUID.randomUUID().toString();
        String userTokenXml = ssoHelper.getUserToken(user, userTicket);

        if (userTokenXml == null) {
            logger.warn("action - getUserToken failed. Redirecting to login.");
            model.addAttribute(SessionHelper.LOGIN_ERROR, "Could not log in.");
            ModelHelper.setEnabledLoginTypes(ssoHelper,model);
            model.addAttribute(SessionHelper.REDIRECT_URI, redirectURI);
            return "login";
        }

        response.addCookie(ssoHelper.createUserTokenCookie(userTokenXml));

        // ticket on redirect
        if (redirectURI.toLowerCase().contains(SessionHelper.USERTICKET)) {
            // Do not overwrite ticket
        } else {
            redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, userTicket);
        }
        // Action use redirect...
        model.addAttribute(SessionHelper.REDIRECT, redirectURI);
        model.addAttribute(SessionHelper.REDIRECT_URI, redirectURI);
        logger.info("action - Redirecting to {}", redirectURI);
        return "action";
    }



    private String getRedirectURI(HttpServletRequest request) {
        String redirectURI = request.getParameter(SessionHelper.REDIRECT_URI);
        logger.trace("getRedirectURI - redirectURI from request: {}", redirectURI);
        if (redirectURI == null || redirectURI.length() < 1) {
            logger.trace("getRedirectURI - No redirectURI found, setting to {}", DEFAULT_REDIRECT);
            return DEFAULT_REDIRECT;
        }
        return redirectURI;
    }

}


