package net.whydah.sso;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.util.UserTokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@Controller
public class SSOLogoutController {
    private static final Logger logger = LoggerFactory.getLogger(SSOLogoutController.class);
    private final UserTokenHandler userTokenHandler;
    private final CookieManager cookieManager;


    public SSOLogoutController() {
        this.userTokenHandler = new UserTokenHandler();
        String cookiedomain = null;
        try {
            cookiedomain = AppConfig.readProperties().getProperty("cookiedomain");
        } catch (IOException e) {
            logger.warn("Could not load cookiedomain property. Using default value.");
        }
        this.cookieManager = new CookieManager(userTokenHandler, cookiedomain);

    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        String redirectURI = request.getParameter("redirectURI");
        String LOGOURL = "/sso/images/site-logo.png";
        try {
            Properties properties = AppConfig.readProperties();
            LOGOURL = properties.getProperty("logourl");
        } catch (Exception e) {
            logger.error("", e);
        }
        model.addAttribute("logoURL", LOGOURL);
        if (redirectURI != null && redirectURI.length() > 3) {
            model.addAttribute("redirect", redirectURI);
        } else {
            model.addAttribute("redirect", "login");
        }

        String usertoken = request.getParameter(CookieManager.USER_TOKEN_REFERENCE_NAME);
        if (usertoken != null && usertoken.length() > 3) {
            model.addAttribute("TokenID", usertoken);
            return "logout";
        } else {
            return "action";
        }

    }

    @RequestMapping("/logoutaction")
    public String logoutAction(HttpServletRequest request, HttpServletResponse response, Model model) {
        //model.
        String usertokenid = request.getParameter(CookieManager.USER_TOKEN_REFERENCE_NAME);
        String redirectURI = request.getParameter("redirectURI");

        if (usertokenid != null && usertokenid.length() > 1) {
            logger.info("logoutAction - releasing usertokenid={}",usertokenid);
            userTokenHandler.releaseUserToken(usertokenid);
        }
        String usertokenidfromcookie = cookieManager.getUserTokenIdFromCookie(request,response).getUsertokenid();
        logger.info("logoutAction - releasing usertokenid={} found in cookie", usertokenidfromcookie);
        userTokenHandler.releaseUserToken(usertokenidfromcookie);

        clearAllWhydahCookies(request, response);

        String LOGOURL="/sso/images/site-logo.png";
        try {
            Properties properties = AppConfig.readProperties();
            LOGOURL = properties.getProperty("logourl");

        } catch (Exception e){

        }
        model.addAttribute("logoURL", LOGOURL);

        model.addAttribute("redirect", redirectURI);
        return "action";
    }

    private void clearAllWhydahCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logger.trace("clearAllWhydahCookies - Found {} cookie(s)", cookies.length);
            for (Cookie cookie : cookies) {
                logger.trace("clearAllWhydahCookies - Checking cookie:" + cookie.getName());
                if (!CookieManager.USER_TOKEN_REFERENCE_NAME.equals(cookie.getName())) {
                    continue;
                }


                String usertokenid = cookie.getValue();
                userTokenHandler.releaseUserToken(usertokenid);
                logger.trace("clearAllWhydahCookies - releaseUserToken  usertokenid: {}  ",usertokenid);
                cookie.setValue("logout");
                response.addCookie(cookie);
                logger.trace("clearAllWhydahCookies - Reset cookie.  usertokenid: {}  Cookie: {}",usertokenid, cookie);
            }
        }
    }

}
