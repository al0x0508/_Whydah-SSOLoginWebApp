package net.whydah.sso.authentication.whydah;

import net.whydah.sso.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CookieManager {
    public static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_sso";
    private static final String LOGOUT_COOKIE_VALUE = "logout";
    private static final Logger logger = LoggerFactory.getLogger(CookieManager.class);

    private static String cookiedomain = null;

    private CookieManager() {
    }

    static {
        try {
            cookiedomain = AppConfig.readProperties().getProperty("cookiedomain");
        } catch (IOException e) {
            logger.warn("AppConfig.readProperties failed. cookiedomain was set to {}", cookiedomain, e);
        }
    }

    public static void createAndSetUserTokenCookie(String userTokenId, HttpServletResponse response) {
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, userTokenId);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        // cookie.setMaxAge(maxAge);
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setPath("/");
        if (cookiedomain != null && !cookiedomain.isEmpty()) {
            cookie.setDomain(cookiedomain);
        }
        cookie.setValue(userTokenId);
        // cookie.setSecure(true);
        logger.trace("Created cookie with name={}, domain={}, value/userTokenId={}, maxAge={}, secure={}",
                cookie.getName(), cookie.getDomain(), userTokenId, cookie.getMaxAge(), cookie.getSecure());

        response.addCookie(cookie);
    }

    public static String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie userTokenCookie = getUserTokenCookie(request);
        return (userTokenCookie != null ? userTokenCookie.getValue() : null);
    }

    public static void clearUserTokenCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getUserTokenCookie(request);
        if (cookie != null) {
            logger.trace("Cleared cookie with name={}, domain={}", cookie.getName(), cookie.getDomain());
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setValue("");
            response.addCookie(cookie);
        }
    }

    private static Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (USER_TOKEN_REFERENCE_NAME.equalsIgnoreCase(cookie.getName()) && cookiedomain.equalsIgnoreCase(cookie.getDomain())) {
                return cookie;
            }
        }
        return null;
    }


    public static void setLogoutUserTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie userTokenCookie = getUserTokenCookie(request);
        if (userTokenCookie != null) {
            userTokenCookie.setValue(LOGOUT_COOKIE_VALUE);
            response.addCookie(userTokenCookie);
        }
    }
}
