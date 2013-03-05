package net.whydah.sso.service;

import net.whydah.sso.service.util.SSOHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SSOLogoutController {
    SSOHelper sso = new SSOHelper();

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        String redirectURI = request.getParameter("redirectURI");
        if (redirectURI != null && redirectURI.length() > 3) {
            model.addAttribute("redirect", redirectURI);
        } else {
            model.addAttribute("redirect", "welcome");
        }

        String usertoken = request.getParameter(SSOHelper.USER_TOKEN_REFERENCE_NAME);
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
        String tokenID = request.getParameter(SSOHelper.USER_TOKEN_REFERENCE_NAME);
        String redirectURI = request.getParameter("redirectURI");

        if (tokenID != null && tokenID.length() > 1) {
            sso.releaseUserToken(tokenID);
        }

        Cookie cookie = new Cookie(SSOHelper.USER_TOKEN_REFERENCE_NAME, "");
        cookie.setMaxAge(100000);
        cookie.setValue("");
        response.addCookie(cookie);

        model.addAttribute("redirect", redirectURI);
        return "action";
    }
}
