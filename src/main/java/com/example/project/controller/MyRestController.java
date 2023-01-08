package com.example.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class MyRestController {

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal OAuth2User user, Principal principal) throws JsonProcessingException {

        try {
            String[] values = user.getAttributes().toString().split(", ");

            List<String> collect = Arrays.stream(values).filter(r -> r.startsWith("email=")).limit(1).collect(Collectors.toList());
            String a = collect.get(0);

            return a.substring(6);
        }catch (NullPointerException nullPointerException){
            try {

                return principal.getName();
            }catch (Exception e){
                System.out.println("Exception");
            }
        }

        return "exve[tionsd";

    }
}
