package net.whydah.sso.authentication;

import net.whydah.sso.config.AppConfig;
import org.springframework.ui.Model;

import java.io.IOException;

public class ModelHelper {
    static LoginTypes enabledLoginTypes;

    static {
        try {
            enabledLoginTypes = new LoginTypes(AppConfig.readProperties());
        } catch (IOException e) {
            enabledLoginTypes = null;     
        }
    }
            
    
    public static void setEnabledLoginTypes(Model model) {
        model.addAttribute("signupEnabled", enabledLoginTypes.isSignupEnabled());
        model.addAttribute("facebookLoginEnabled", enabledLoginTypes.isFacebookLoginEnabled());
        model.addAttribute("openidLoginEnabled", enabledLoginTypes.isOpenIdLoginEnabled());
        model.addAttribute("omniLoginEnabled", enabledLoginTypes.isOmniLoginEnabled());
        model.addAttribute("netIQLoginEnabled", enabledLoginTypes.isNetIQLoginEnabled());
        model.addAttribute("userpasswordLoginEnabled", enabledLoginTypes.isUserpasswordLoginEnabled());

        if (enabledLoginTypes.isNetIQLoginEnabled()) {
            setNetIQOverrides(model);
        }
    }

    private static void setNetIQOverrides(Model model) {
        try {
            model.addAttribute("netIQtext", AppConfig.readProperties().getProperty("logintype.netiq.text"));
            model.addAttribute("netIQimage", AppConfig.readProperties().getProperty("logintype.netiq.logo"));
        } catch (IOException ioe) {
            model.addAttribute("netIQtext", "NetIQ");
            model.addAttribute("netIQimage", "images/netiqlogo.png");
        }
    }
}