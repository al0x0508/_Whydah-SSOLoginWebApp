package net.whydah.sso.authentication.whydah;

import net.whydah.sso.usertoken.UserTokenHandler;
import net.whydah.sso.usertoken.UserTokenXpathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CookieManager {
    public static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_sso";
    public static String cookiedomain = ".whydah.net";

    private static final Logger logger = LoggerFactory.getLogger(CookieManager.class);


    private final UserTokenHandler userTokenHandler;


    public CookieManager(UserTokenHandler userTokenHandler, String cookiedomain) {
        this.userTokenHandler = userTokenHandler;
        if (cookiedomain != null && !cookiedomain.isEmpty()) {
            CookieManager.cookiedomain = cookiedomain;
        }

    }

    public static Cookie createUserTokenCookie(String userTokenXml) {
        String usertokenID = UserTokenXpathHelper.getUserTokenId(userTokenXml);
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, usertokenID);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        // cookie.setMaxAge(maxAge);
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setDomain(cookiedomain);
        cookie.setValue(usertokenID);
        // cookie.setSecure(true);
        logger.debug("Created cookie with name=" + cookie.getName() + ", usertokenID=" + cookie.getValue() + ", maxAge=" + cookie.getMaxAge()+", domain"+cookiedomain);
        return cookie;
    }

    /*
    public static String getCookieDomain() {
        logger.info("CookieDomain: " + cookiedomain);
        return cookiedomain;
    }
    */

    public WhydahUserTokenId getUserTokenIdFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        boolean found = false;
        WhydahUserTokenId foundTokenId = WhydahUserTokenId.fromTokenId("");
        if (cookies != null) {
            logger.trace("getUserTokenIdFromCookie - Found {} cookie(s)", cookies.length);
            for (Cookie cookie : cookies) {
                logger.trace("getUserTokenIdFromCookie - Checking cookie:"+cookie.getName());
                if (!CookieManager.USER_TOKEN_REFERENCE_NAME.equals(cookie.getName())) {
                    continue;
                }

                String usertokenId = cookie.getValue();
                logger.trace("getUserTokenIdFromCookie - Found whydahusertoken cookie, usertokenid={}", usertokenId);
                if ("logout".equalsIgnoreCase(usertokenId)) {

                    // TODO: should probably clear the logout cookie here?

                    return WhydahUserTokenId.invalidTokenId();
                }
                if (userTokenHandler.verifyUserTokenId(usertokenId)) {
                    logger.trace("getUserTokenIdFromCookie - usertokenid ok");
                    foundTokenId = WhydahUserTokenId.fromTokenId(usertokenId);
                    found = true;
                } else {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setPath("/");
                    logger.trace("Cleared cookie with invalid usertokenid");
                    response.addCookie(cookie);
                }
            }
        }
        if (found) {
            return foundTokenId;
        }
        logger.debug("getUserTokenIdFromCookie - Found no cookies with usertokenid");
        return WhydahUserTokenId.invalidTokenId();
    }
}
