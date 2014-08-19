package net.whydah.sso;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.data.UserCredential;
import net.whydah.sso.data.UserNameAndPasswordCredential;
import net.whydah.sso.util.SSOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

@Controller
public class NewUserController {

    private static final Logger logger = LoggerFactory.getLogger(NewUserController.class);
    private final SSOHelper ssoHelper = new SSOHelper();
    String LOGOURL = "/sso/images/site-logo.png";

    public NewUserController() throws IOException {
        Properties properties = AppConfig.readProperties();
        String MY_APP_URI = properties.getProperty("myuri");
        LOGOURL = properties.getProperty("logourl");
    }

    @RequestMapping("/signup")
    public String newUser(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {
        logger.trace("/signup entry");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        if (email!=null && username != null) {
            logger.info("Requested signup - email:" + email + "  username:" + username);
            // TODO   Post signup-request to UAS
        }


        model.addAttribute("logoURL", LOGOURL);
        return "newuser";
    }

    @RequestMapping("/createnewuser")
    public String createNewUser(HttpServletRequest request, HttpServletResponse response, Model model) throws MalformedURLException {
        logger.trace("/createnewuser entry");
        model.addAttribute("logoURL", LOGOURL);
        String fbId = "";
        String username = "user";
        UserCredential userCredential = new UserCredential() {
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
            ModelHelper.setEnabledLoginTypes(ssoHelper,model);
            return "login";
        }
        String clientRedirectURI = request.getParameter("redirectURI");
        model.addAttribute("logoURL", LOGOURL);

        if (clientRedirectURI == null || clientRedirectURI.length() < 3) {
            model.addAttribute("redirect", "welcome");
        } else {
            model.addAttribute("redirect", clientRedirectURI);
        }
        return "action";
    }

}
