package com.example.project.config;

import com.example.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class UserConfiguration {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain securityFilterChain2(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher("/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(r -> r
                        .anyRequest().authenticated())
                .oauth2Login().defaultSuccessUrl("/myauth", true)
                .permitAll().and().build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(r -> r.requestMatchers("/todelete", "/rest/**", "/login", "/login", "/register", "/reset", "/resetPassword", "/changePassword/**",  "/css/*", "/js/*", "/img/*", "/confirm/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(r -> r.loginPage("/login").permitAll().failureHandler(new CustomAuthenticationFailureHandler()).defaultSuccessUrl("/posts", true))
                .logout(r -> r.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).invalidateHttpSession(true).clearAuthentication(true))
                .build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
