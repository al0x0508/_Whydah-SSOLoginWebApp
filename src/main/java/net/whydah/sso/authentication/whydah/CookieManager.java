package net.whydah.sso.authentication.whydah;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CookieManager {
    public static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_sso";
    private static final String LOGOUT_COOKIE_VALUE = "logout";
    private static final Logger logger = LoggerFactory.getLogger(CookieManager.class);

    public static String cookiedomain = ".whydah.net";

    public CookieManager(String cookiedomain) {
        if (cookiedomain != null && !cookiedomain.isEmpty()) {
            CookieManager.cookiedomain = cookiedomain;
        }

    }

    public static Cookie createUserTokenCookie(String userTokenId) {
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, userTokenId);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        // cookie.setMaxAge(maxAge);
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setDomain(cookiedomain);
        cookie.setValue(userTokenId);
        // cookie.setSecure(true);
        logger.trace("Created cookie with name={}, domain={}, value/userTokenId={}, maxAge={}, secure={}", cookie.getName(), cookie.getDomain(), userTokenId, cookie.getMaxAge(), cookie.getSecure());
        return cookie;
    }

    /*
    public static String getCookieDomain() {
        logger.info("CookieDomain: " + cookiedomain);
        return cookiedomain;
    }
    */

    /*
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
                if (tokenServiceClient.verifyUserTokenId(usertokenId)) {
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
    */

    public static String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie userTokenCookie = getUserTokenCookie(request);
        return (userTokenCookie != null ? userTokenCookie.getValue() : null);
    }
    private static Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (CookieManager.USER_TOKEN_REFERENCE_NAME.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }


    public static void clearUserTokenCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                logger.trace("Cleared cookie with name={}", cookie.getName());
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setValue("");
                response.addCookie(cookie);
            }
        }
    }

    public static void setLogoutUserTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie userTokenCookie = getUserTokenCookie(request);
        if (userTokenCookie == null) {
            return;
        }
        userTokenCookie.setValue(LOGOUT_COOKIE_VALUE);
        response.addCookie(userTokenCookie);
    }
}
