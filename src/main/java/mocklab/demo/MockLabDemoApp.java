package mocklab.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

public class MockLabDemoApp {}

//@SpringBootApplication
//@Controller
//public class MockLabDemoApp extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests(a -> a
//                        .antMatchers("/", "/login", "/oauth2/**", "/openid-connect", "/todo/**", "/todo-items/**", "/error", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(e -> e
//                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
//                )
//                .logout(l -> l
//                        .logoutSuccessUrl("/login").permitAll()
//                )
//                .oauth2Login(customizer -> customizer.successHandler(successHandler()));
//    }
//
//    public AuthenticationSuccessHandler successHandler() {
//        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
//        handler.setDefaultTargetUrl("/user");
//        return handler;
//    }
//
//    @GetMapping("/")
//    public ModelAndView getRootPage() {
//        return new ModelAndView("index");
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(MockLabDemoApp.class, args);
//    }
//
//}
