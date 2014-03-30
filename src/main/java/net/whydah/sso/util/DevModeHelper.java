package net.whydah.sso.util;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.UUID;


public class DevModeHelper {

    public static String return_DEV_MODE_ExampleUserToken(int n){
        return "/dev_usertoken.1.ftl";
    }

    public static String return_DEV_MODE_ExampleApplicationToken(int n){
        return "/dev_applicationtoken.1.ftl";
    }

    public static String return_actionWithRedirectURI(String redirectURI,SSOHelper ssoHelper,Model model){
        String ticketID = UUID.randomUUID().toString();
        if (redirectURI.toLowerCase().contains("userticket")) {
            // Do not overwrite ticket
        } else {
            redirectURI = ssoHelper.appendTicketToRedirectURI(redirectURI, ticketID);

        }
        model.addAttribute("redirectURI", redirectURI);
        return "action";
    }

}
