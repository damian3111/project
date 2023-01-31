package com.example.project.controller;

import com.example.project.entity.AppUser;
import com.example.project.entity.AppUserRole;
import com.example.project.entity.ConfirmationToken;
import com.example.project.entity.PasswordResetToken;
import com.example.project.repository.PasswordResetTokenRepository;
import com.example.project.repository.TokenRepository;
import com.example.project.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class MyRestController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


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

    @PostMapping("/rest/saveUser")
    public AppUser saveUser(@RequestBody String email) throws Exception {

        System.out.println("reererreererrer" + email);

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setAppUserRole(AppUserRole.USER);
        user.setEnabled(true);
        user.setLastName("Kowalski");
        user.setFirstName("Jan");
        user.setPassword("password");
        user.setLocked(false);

        return userRepository.save(user);

    }


    @GetMapping("/rest/getUser")
    public AppUser getUserById(@RequestBody long id) throws Exception {

        System.out.println("reererreererrer" + id);
        List<AppUser> all = userRepository.findAll();
        all.forEach(a -> System.out.println("DADADADDADADADADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + a));

        AppUser user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        System.out.println("erqqqqqqqqq" + user);
        return user;

        }

    @PostMapping("/rest/saveToken")
    public ConfirmationToken saveToken(@RequestBody String token, @RequestParam("minutes") int minutes){

        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz" + minutes);

        AppUser appUser = new AppUser();
        appUser.setEmail("janKowalski@gmail.com");
        appUser.setFirstName("Jan");
        appUser.setLastName("Kowalski");
        appUser.setAppUserRole(AppUserRole.USER);
        appUser.setEnabled(false);
        appUser.setPassword("password");
        userRepository.save(appUser);

        ConfirmationToken confirmationToken2 = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(minutes), appUser);
        confirmationToken2.setConfirmedAt(null);

        tokenRepository.save(confirmationToken2);

        return tokenRepository.save(confirmationToken2);
    }

    @GetMapping("/rest/getToken")
    public ConfirmationToken getConfirmationTokenByToken(@RequestBody String token){
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token).orElseThrow();
        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        System.out.println(confirmationToken);
        return confirmationToken;
    }

    @PostMapping("/rest/saveResetPasswordToken")
    public PasswordResetToken saveResetPasswordToken() throws Exception {

        AppUser user = userRepository.findByEmail("janKowalski@gmial.com").orElseThrow(() -> new Exception("User not found"));

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken("passwordResetToken");
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetToken.setAppUser(user);

        return passwordResetTokenRepository.save(passwordResetToken);
    }


}
