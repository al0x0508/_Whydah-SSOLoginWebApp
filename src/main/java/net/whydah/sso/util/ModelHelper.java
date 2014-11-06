package net.whydah.sso.util;

import net.whydah.sso.config.AppConfig;
import org.springframework.ui.Model;

import java.io.IOException;

public class ModelHelper {
    public static void setEnabledLoginTypes(UserTokenHandler userTokenHandler,Model model) {
        model.addAttribute("signupEnabled", userTokenHandler.getEnabledLoginTypes().isSignupEnabled());
        model.addAttribute("facebookLoginEnabled", userTokenHandler.getEnabledLoginTypes().isFacebookLoginEnabled());
        model.addAttribute("openidLoginEnabled", userTokenHandler.getEnabledLoginTypes().isOpenIdLoginEnabled());
        model.addAttribute("omniLoginEnabled", userTokenHandler.getEnabledLoginTypes().isOmniLoginEnabled());
        model.addAttribute("netIQLoginEnabled", userTokenHandler.getEnabledLoginTypes().isNetIQLoginEnabled());
        model.addAttribute("userpasswordLoginEnabled", userTokenHandler.getEnabledLoginTypes().isUserpasswordLoginEnabled());

        if (userTokenHandler.getEnabledLoginTypes().isNetIQLoginEnabled()) {
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