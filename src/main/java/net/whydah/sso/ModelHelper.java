package net.whydah.sso;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.util.SSOHelper;
import org.springframework.ui.Model;

import java.io.IOException;

public class ModelHelper {

    public ModelHelper(net.whydah.sso.SSOLoginController SSOLoginController) {
    }

    public static void setEnabledLoginTypes(SSOHelper ssoHelper,Model model) {
        model.addAttribute("signupEnabled", ssoHelper.getEnabledLoginTypes().isSignupEnabled());
        model.addAttribute("facebookLoginEnabled", ssoHelper.getEnabledLoginTypes().isFacebookLoginEnabled());
        model.addAttribute("openidLoginEnabled", ssoHelper.getEnabledLoginTypes().isOpenIdLoginEnabled());
        model.addAttribute("omniLoginEnabled", ssoHelper.getEnabledLoginTypes().isOmniLoginEnabled());
        model.addAttribute("netIQLoginEnabled", ssoHelper.getEnabledLoginTypes().isNetIQLoginEnabled());
        model.addAttribute("userpasswordLoginEnabled", ssoHelper.getEnabledLoginTypes().isUserpasswordLoginEnabled());

        if (ssoHelper.getEnabledLoginTypes().isNetIQLoginEnabled()) {
            setNetIQOverrides(model);
        }
    }

    public static void setNetIQOverrides(Model model) {
        try {
            model.addAttribute("netIQtext", AppConfig.readProperties().getProperty("logintype.netiq.text"));
            model.addAttribute("netIQimage", AppConfig.readProperties().getProperty("logintype.netiq.logo"));
        } catch (IOException ioe) {
            model.addAttribute("netIQtext", "NetIQ");
            model.addAttribute("netIQimage", "images/netiqlogo.png");
        }
    }
}