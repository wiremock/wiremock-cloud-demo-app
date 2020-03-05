package mocklab.demo.oauth2;

import com.google.common.collect.ImmutableMap;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OAuth2DemoResource {

    @GetMapping("/user")
    public ModelAndView getUserInfoPage(@AuthenticationPrincipal OAuth2User user) {
        ModelAndView modelAndView = new ModelAndView("userinfo");
        modelAndView.addObject("user", ImmutableMap.of(
                "email", user.getAttribute("email"),
                "id", user.getAttribute("sub")
        ));

        return modelAndView;
    }

    @GetMapping("/login")
    public String getSsoPage(@RequestParam(value = "type", required = false) String loginType, Model model) {
        if (loginType == null) {
            return "redirect:/login?type=mock";
        }

        boolean real = loginType.equals("real");
        String toggleType = real ? "mock" : "real";
        String loginProvider = real ? "google" : "mock-oidc";

        model.addAttribute("loginType", loginType)
             .addAttribute("toggleType", toggleType)
             .addAttribute("loginProvider", loginProvider)
             .addAttribute("real", real);

        return "login";
    }
}
