package net.whydah.sso.service;

import net.whydah.sso.service.data.UserCredential;
import net.whydah.sso.service.util.SSOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;


public class NewUserController {

    private static final Logger logger = LoggerFactory.getLogger(NewUserController.class);
    private final SSOHelper ssoHelper = new SSOHelper();

    @RequestMapping("/newuser")
    public String newUser(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {
        return "newuser";

    }

    @RequestMapping("/createnewuser")
    public String createNewUser(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {
        String fbId="";
        String username="user";
        UserCredential userCredential= new UserCredential() {
            @Override
            public String toXML() {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                        "<usercredential>\n" +
                        "    <params>\n" +
                        "        <fbId>" + "" + "</fbId>\n" +
                        "        <username>" + "user" + "</username>\n" +
                        "    </params> \n" +
                        "</usercredential>\n";
            }
        };



            String userTokenXml = ssoHelper.createAndLogonUser(null, "", userCredential, "");
            if (userTokenXml == null) {
                logger.error("createAndLogonUser failed. Redirecting to login page.");
                String redirectURI = "";
                model.addAttribute("redirectURI", redirectURI);
                model.addAttribute("loginError", "Login error: Could not create or authenticate user.");
                return "login";
            }



        model.addAttribute("redirect", "welcome");
        return "action";
    }

}
