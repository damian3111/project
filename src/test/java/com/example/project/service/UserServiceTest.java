package com.example.project.service;

import com.example.project.config.MyPasswordEncoder;
import com.example.project.dto.RegistrationRequest;
import com.example.project.email.EmailSender;
import com.example.project.entity.AppUser;
import com.example.project.entity.AppUserRole;
import com.example.project.repository.TokenRepository;
import com.example.project.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService underTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder bCryptPasswordEncoder;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private EmailSender emailSender;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private Principal principal;


    @BeforeEach
    void tearDown() {
        underTest = new UserService(userRepository, bCryptPasswordEncoder,tokenRepository, emailSender);
    }

    @Test
    void itShouldReturnUser() {
        //given
        String email = "janKowalski@gmail.com";

        AppUser user = new AppUser("Jan", "Kowalski",
                email, "password", AppUserRole.USER);
        user.setEnabled(true);

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));

        //when
        UserDetails userDetails = underTest.loadUserByUsername(email);

        //then
        assertThat(userDetails.getUsername()).isEqualTo(email);

    }

    @Test
    void itShouldntReturnUser() {
        //given
        String email = "janKowalski@gmail.com";

        AppUser user = new AppUser("Jan", "Kowalski",
                email, "password", AppUserRole.USER);

        user.setEnabled(false);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> underTest.loadUserByUsername(email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("User is disabled");


    }

    @Test
    void itShouldReturnUserEmailFromOAuth2User() {

        //given
        Map<String, Object> map = new HashMap<>();
        String email = "janKowalski@gmail.com";
        map.put("name", "Jan");
        map.put("email", email);

        given(oAuth2User.getAttributes()).willReturn(map);

        //when
        String result = underTest.findEmail(oAuth2User, principal);

        //then
        assertThat(result).isEqualTo(email);
        then(principal).shouldHaveNoInteractions();
    }

    @Test
    void itShouldReturnUserEmailFromPrincipal() {

        //given
        String email = "janKowalski@gmail.com";

        given(oAuth2User.getAttributes()).willReturn(null);
        given(principal.getName()).willReturn(email);

        //when
        String result = underTest.findEmail(oAuth2User, principal);

        //then
        assertThat(result).isEqualTo(email);

    }

    @ParameterizedTest
    @CsvSource({"janKowalski@gmail.com, TRUE", "filipDabrowski@onet.pl, TRUE", "janKowal@o2.pl, TRUE",
            "janKowalski12312312312312132@gmail.com, FALSE", "filipDabrowski@onetonet.net.pl, FALSE", "janKowal@o2.plplpl, FALSE"})
    void parametrizedTestIfEmailIsValid(String email, boolean expected) {
        //given
        //when
        boolean result = underTest.emailValidation(email);

        //then
        assertThat(result).isEqualTo(expected);
    }

}