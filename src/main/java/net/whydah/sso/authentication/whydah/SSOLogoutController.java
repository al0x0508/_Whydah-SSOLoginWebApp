package net.whydah.sso.authentication.whydah;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.usertoken.TokenServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@Controller
public class SSOLogoutController {
    private static final Logger logger = LoggerFactory.getLogger(SSOLogoutController.class);
    private final TokenServiceClient tokenServiceClient;


    public SSOLogoutController() {
        this.tokenServiceClient = new TokenServiceClient();
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

        String userTokenId = request.getParameter(CookieManager.USER_TOKEN_REFERENCE_NAME);
        if (userTokenId != null && userTokenId.length() > 3) {
            model.addAttribute("TokenID", userTokenId);
            return "logout";
        } else {
            return "action";
        }

    }

    @RequestMapping("/logoutaction")
    public String logoutAction(HttpServletRequest request, HttpServletResponse response, Model model) {
        String userTokenIdFromRequest = request.getParameter(CookieManager.USER_TOKEN_REFERENCE_NAME);

        if (userTokenIdFromRequest != null && userTokenIdFromRequest.length() > 1) {
            logger.debug("logoutAction - releasing userTokenIdFromRequest={} from", userTokenIdFromRequest);
            tokenServiceClient.releaseUserToken(userTokenIdFromRequest);
        } else {
            String userTokenIdFromCookie = CookieManager.getUserTokenIdFromCookie(request);
            if (userTokenIdFromCookie != null && userTokenIdFromCookie.length() > 1) {
                logger.debug("logoutAction - releasing userTokenIdFromCookie={}", userTokenIdFromCookie);
                tokenServiceClient.releaseUserToken(userTokenIdFromCookie);
            } else {
                logger.warn("logoutAction - tokenServiceClient.releaseUserToken was not called because no userTokenId was found in request or cookie.");
            }
        }


        CookieManager.setLogoutUserTokenCookie(request, response);

        String LOGOURL;
        try {
            Properties properties = AppConfig.readProperties();
            LOGOURL = properties.getProperty("logourl");
        } catch (IOException e){
            LOGOURL = "/sso/images/site-logo.png";
        }
        model.addAttribute("logoURL", LOGOURL);

        String redirectURI = request.getParameter("redirectURI");
        model.addAttribute("redirect", redirectURI);
        return "action";
    }
}
