package com.example.project.config;

import java.io.IOException;
import java.util.Locale;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;


@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {



    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        setDefaultFailureUrl("/login");

        super.onAuthenticationFailure(request, response, exception);


        String errorMessage="";
        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            errorMessage = "Confirm your email";
            System.out.println("user disabled");
            request.getSession().setAttribute("username1", request.getParameter("username"));
        } else if(exception.getClass() == BadCredentialsException.class){
            System.out.println("email or passwor dwrong");

            errorMessage = "Email or password wrong";
        }

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
