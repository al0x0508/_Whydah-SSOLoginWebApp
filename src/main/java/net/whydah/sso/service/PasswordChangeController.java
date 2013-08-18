package net.whydah.sso.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.whydah.sso.service.config.AppConfig;
import net.whydah.sso.service.data.PasswordChangeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Password management self service.
 */
@Controller
public class PasswordChangeController {
    private static final Logger log = LoggerFactory.getLogger(PasswordChangeController.class);
    private static final Client uibClient = Client.create();
    private URI uibServiceUri;

    //TODO Should go via TokenService.
    public PasswordChangeController() throws IOException {
        uibServiceUri = UriBuilder.fromUri(AppConfig.readProperties().getProperty("useridentitybackend")).build();
    }

    @RequestMapping("/test/*")
    public String t1(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @RequestMapping("/resetpassword")
    public String resetpassword(HttpServletRequest request, Model model) {
        log.trace("resetpassword was called");
        String user = request.getParameter("user");
        if(user == null) {
            return "resetpassword";
        }

        WebResource uibWR = uibClient.resource(uibServiceUri).path("/useradmin/users/" + user + "/resetpassword");
        ClientResponse response = uibWR.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        if(response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            String error = response.getEntity(String.class);
            log.error(error);
            model.addAttribute("error", error);
            return "resetpassword";
        }
        return "resetpassworddone";
    }

    @RequestMapping("/changepassword/*")
    public String changePasswordFromLink(HttpServletRequest request, Model model) {
        log.trace("changePasswordFromLink was called");
        PasswordChangeToken passwordChangeToken = getTokenFromPath(request);
        model.addAttribute("user", passwordChangeToken.getUser());
        model.addAttribute("token", passwordChangeToken.getToken());
        if(!passwordChangeToken.isValid()) {
            return "changepasswordtokentimedout";
        } else {
            return "changepassword";
        }
    }

    @RequestMapping("/dochangepassword/*")
    public String doChangePasswordFromLink(HttpServletRequest request, Model model) {
        log.trace("doChangePasswordFromLink was called");
        PasswordChangeToken passwordChangeToken = getTokenFromPath(request);
        String newpassword = request.getParameter("newpassword");
        //System.out.println(newpassword);
        WebResource uibWR = uibClient.resource(uibServiceUri).path("/useradmin/users/" + passwordChangeToken.getUser() + "/newpassword/" + passwordChangeToken.getToken());
        System.out.println("kaller " + uibWR.getURI());
        ClientResponse response = uibWR.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,"{\"newpassword\":\"" + newpassword + "\"}");
        model.addAttribute("user", passwordChangeToken.getUser());
        if(response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            String error = response.getEntity(String.class);
            //System.out.println(response);
            log.error(error);
            model.addAttribute("error", error);
            model.addAttribute("token", passwordChangeToken.getToken());
            return "changepassword";
        }
        return "changedpassword";
    }

    private PasswordChangeToken getTokenFromPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        String tokenString = path.substring(path.lastIndexOf('/') + 1);
        return new PasswordChangeToken(tokenString);
    }

}
