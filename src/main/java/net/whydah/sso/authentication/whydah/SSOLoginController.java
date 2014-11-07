package net.whydah.sso.authentication.whydah;

import net.whydah.sso.authentication.ModelHelper;
import net.whydah.sso.authentication.UserCredential;
import net.whydah.sso.config.AppConfig;
import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.usertoken.TokenServiceClient;
import net.whydah.sso.usertoken.UserTokenXpathHelper;
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
    public static final String DEFAULT_REDIRECT = "welcome";

    private final static Logger logger = LoggerFactory.getLogger(SSOLoginController.class);
    private final TokenServiceClient tokenServiceClient;
    private String LOGOURL = "/sso/images/site-logo.png";

    //private final int MIN_REDIRECT_SIZE=4;
    //private final ModelHelper modelHelper = new ModelHelper(this);


    public SSOLoginController() throws IOException {
        Properties properties = AppConfig.readProperties();
        //String MY_APP_URI = properties.getProperty("myuri");
        LOGOURL = properties.getProperty("logourl");

        this.tokenServiceClient = new TokenServiceClient();
    }


    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response,Model model) {
        String redirectURI = getRedirectURI(request);
        logger.trace("login - Found redirectURI: {}", redirectURI);
        model.addAttribute(SessionHelper.LOGO_URL, LOGOURL);
        model.addAttribute(SessionHelper.REDIRECT_URI, redirectURI);


        //usertokenId = cookieManager.getUserTokenIdFromCookie(request, response);
        String userTokenIdFromCookie = CookieManager.getUserTokenIdFromCookie(request);
        WhydahUserTokenId whydahUserTokenId = WhydahUserTokenId.invalidTokenId();
        if ("logout".equalsIgnoreCase(userTokenIdFromCookie)) {
            //TODO
            logger.trace("userTokenId={} from cookie. TODO: should probably clear the logout cookie here?", userTokenIdFromCookie);
            //usertokenId = WhydahUserTokenId.invalidTokenId();
        } else if (tokenServiceClient.verifyUserTokenId(userTokenIdFromCookie)) {
            logger.trace("userTokenId={} from cookie verified OK.", userTokenIdFromCookie);
            whydahUserTokenId = WhydahUserTokenId.fromTokenId(userTokenIdFromCookie);
        } else {
            CookieManager.clearUserTokenCookies(request, response);
        }

        logger.trace("login - Found usertokenid={} from whydah cookie", whydahUserTokenId);
        if (whydahUserTokenId.isValid()) {
            logger.trace("login - Found usertokenid={} is valid", whydahUserTokenId);

            if (DEFAULT_REDIRECT.equalsIgnoreCase(redirectURI)){
                logger.trace("login - Did not find any sensible redirectURI, using /welcome");
                model.addAttribute(SessionHelper.REDIRECT, redirectURI);
                logger.info("login - Redirecting to {}", redirectURI);
                return "action";

            }
            String userTicket = UUID.randomUUID().toString();
            if (tokenServiceClient.createTicketForUserTokenID(userTicket, whydahUserTokenId.toString())){
                logger.info("login - created new userticket={} for usertokenid={}",userTicket, whydahUserTokenId);
                redirectURI = tokenServiceClient.appendTicketToRedirectURI(redirectURI, userTicket);

                // Action use redirect - not redirectURI
                model.addAttribute(SessionHelper.REDIRECT, redirectURI);
                logger.info("login - Redirecting to {}", redirectURI);
                return "action";
            }

        }
        ModelHelper.setEnabledLoginTypes(model);
        return "login";
    }



    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, HttpServletResponse response,Model model) {
        String userTicket = request.getParameter(TokenServiceClient.USERTICKET);
        model.addAttribute(SessionHelper.LOGO_URL, LOGOURL);
        model.addAttribute(SessionHelper.IAM_MODE, ApplicationMode.getApplicationMode());
        if (userTicket != null && userTicket.length() > 3) {
            logger.trace("welcome - Using userticket");
            model.addAttribute(TokenServiceClient.USERTICKET, userTicket);
            String userToken= tokenServiceClient.getUserTokenByUserTicket(userTicket);
            model.addAttribute(TokenServiceClient.USERTOKEN, userToken);
            model.addAttribute(TokenServiceClient.REALNAME, UserTokenXpathHelper.getRealName(userToken));
            model.addAttribute(TokenServiceClient.USER_TOKEN_ID, UserTokenXpathHelper.getUserTokenId(userToken) );
            return "welcome";
        }
        String userTokenId = CookieManager.getUserTokenIdFromCookie(request);
        if (userTokenId != null && userTokenId.length() > 3) {
            logger.trace("welcome - No userticket, using usertokenID from cookie");
            model.addAttribute(TokenServiceClient.USERTICKET, "No userticket, using usertokenID");
            model.addAttribute(TokenServiceClient.USER_TOKEN_ID, userTokenId);
            String userToken= tokenServiceClient.getUserTokenByUserTokenID(userTokenId);
            model.addAttribute(TokenServiceClient.REALNAME, UserTokenXpathHelper.getRealName(userToken));
            model.addAttribute(TokenServiceClient.USERTOKEN,userToken );
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
        String userTokenXml = tokenServiceClient.getUserToken(user, userTicket);

        if (userTokenXml == null) {
            logger.warn("action - getUserToken failed. Redirecting to login.");
            model.addAttribute(SessionHelper.LOGIN_ERROR, "Could not log in.");
            ModelHelper.setEnabledLoginTypes(model);
            model.addAttribute(SessionHelper.REDIRECT_URI, redirectURI);
            return "login";
        }

        String userTokenId = UserTokenXpathHelper.getUserTokenId(userTokenXml);
        CookieManager.createAndSetUserTokenCookie(userTokenId, response);

        // ticket on redirect
        if (redirectURI.toLowerCase().contains(SessionHelper.USERTICKET)) {
            // Do not overwrite ticket
        } else {
            redirectURI = tokenServiceClient.appendTicketToRedirectURI(redirectURI, userTicket);
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


