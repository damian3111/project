package com.example.project.integration;

import com.example.project.dto.*;
import com.example.project.entity.AppUser;
import com.example.project.entity.AppUserRole;
import com.example.project.entity.ConfirmationToken;
import com.example.project.repository.TokenRepository;
import com.example.project.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach(){
        tokenRepository.deleteAll();
    }

    @Test
    void itShouldSaveUserAndRedirectToRegistrationPage() throws Exception {
        //given
        String email = "janKowalski@gmail.com";

        RegistrationRequest registrationRequest = new RegistrationRequest("Jan", "Kowalski", email, "passPASS123!@#", "passPASS123!@#");

        //when
        MvcResult mvcResult = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
                .flashAttr("user", registrationRequest)
        ).andReturn();
        int status = mvcResult.getResponse().getStatus();

        ResultActions result1 = mockMvc.perform(get("/rest/getUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectToJson(1L)));


        //then
        result1.andExpect(status().isOk());

        assertThat(status).isEqualTo(302);

        assertThat(result1.andReturn().getResponse().getContentAsString()).contains(String.format("\"email\":\"%1$s\"", email));

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).isEqualTo("/register");

    }

    @Test
    void itShouldRedirectToRegistrationPage() throws Exception {
        //given

        String email = "janKowalski@gmail.com";
        RegistrationRequest registrationRequest = new RegistrationRequest("Jan", "Kowalski", email, "pass", "passPASS123!@#");
        //when

        MvcResult mvcResult = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
                .flashAttr("user", registrationRequest)
        ).andReturn();

        //then
        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(302);

        assertThat(mvcResult.getFlashMap().toString()).contains("attributes={tabE=Pass the same password}");
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo("/register");
    }

    @Test
    void itShouldRedirectToRegistrationPage2() throws Exception {
        //given
        String email = "janKowalski@gmail.com";
        RegistrationRequest registrationRequest = new RegistrationRequest("Jan", "Kowalski", email, "pass", "pass");

        //when

        MvcResult result = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
                .flashAttr("user", registrationRequest)
        ).andReturn();

        //then
        System.out.println(result.getFlashMap());

        assertThat(result.getFlashMap().toString()).contains("attributes={tabE=[Password must be 8 or more characters in length., Password must contain 1 or more uppercase characters., Password must contain 1 or more digit characters., Password must contain 1 or more special characters.");
        assertThat(result.getResponse().getStatus()).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/register");

    }

    @Test
    void itShouldntPassTheEmailValidationAndRedirectToRegistrationPage() throws Exception {
        //given
        String email = "@gmail.com";
        RegistrationRequest registrationRequest = new RegistrationRequest("Jan", "Kowalski", email, "passPASS123!@#", "passPASS123!@#");

        //whem
        MvcResult result = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
                .flashAttr("user", registrationRequest)
        ).andReturn();

        //then
        int status = result.getResponse().getStatus();
        assertThat(status).isEqualTo(302);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/register");
        assertThat(result.getFlashMap().toString()).contains("Incorrect email address");

    }

    @Test
    void itShouldConfirmToken() throws Exception {
        //given
        String token = "testTokenString";
        int minutes = 20;

        //when
        MvcResult result2 = mockMvc.perform(post("/rest/saveToken")
                        .with(csrf())
                        .content(token)
                        .param("minutes", String.valueOf(minutes)))
                .andReturn();

        MvcResult result = mockMvc.perform(get("/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
                        .param("token", token))
                .andReturn();

        MvcResult result3 = mockMvc.perform(get("/rest/getToken")
                        .with(csrf())
                        .content(token))
                .andReturn();


        int status2 = result2.getResponse().getStatus();
        int status = result.getResponse().getStatus();
        int status3 = result3.getResponse().getStatus();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TokenDeserialization confirmationToken = objectMapper.readValue(result3.getResponse().getContentAsString(), TokenDeserialization.class);
        //then
        assertThat(status).isEqualTo(302);
        assertThat(status2).isEqualTo(200);
        assertThat(status3).isEqualTo(200);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("register/registration");

        assertThat(confirmationToken.getAppUser().getEnabled()).isEqualTo(true);
        assertThat(confirmationToken.getConfirmedAt()).isNotNull();


    }


    @Test
    void itShouldntConfirmToken() throws Exception {
        //given
        String token = "testTokenString";
        int minutes = -20;

        //when
        MvcResult result2 = mockMvc.perform(post("/rest/saveToken")
                        .with(csrf())
                        .content(token)
                        .param("minutes", String.valueOf(minutes)))
                .andReturn();

        MvcResult result = mockMvc.perform(get("/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
                        .param("token", token))
                .andReturn();

        MvcResult result3 = mockMvc.perform(get("/rest/getToken")
                        .with(csrf())
                        .content(token))
                .andReturn();


        int status2 = result2.getResponse().getStatus();
        int status = result.getResponse().getStatus();
        int status3 = result3.getResponse().getStatus();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TokenDeserialization confirmationToken = objectMapper.readValue(result3.getResponse().getContentAsString(), TokenDeserialization.class);
        //then
        assertThat(status).isEqualTo(302);
        assertThat(status2).isEqualTo(200);
        assertThat(status3).isEqualTo(200);
        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/login");

        assertThat(confirmationToken.getAppUser().getEnabled()).isEqualTo(false);
        assertThat(confirmationToken.getConfirmedAt()).isNull();


    }

    @Test
    void itShouldSendResetPasswordEmail() throws Exception {
        //given
        String email = "janKowalski@gmial.com";
        ResetEmailRequest resetEmailRequest = new ResetEmailRequest(email);

        //when
        MvcResult saveUserResult = mockMvc.perform(post("/rest/saveUser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(email))
                .andReturn();

        MvcResult result1 = mockMvc.perform(get("/reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MvcResult result2 = mockMvc.perform(post("/reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("resetE", resetEmailRequest))
                .andReturn();



        int result1Status = result1.getResponse().getStatus();
        String result1Return = result1.getModelAndView().getViewName();

        String result2Return = result2.getResponse().getRedirectedUrl();

        //then
        assertThat(result1Status).isEqualTo(200);
        assertThat(result1Return).isEqualTo("reset/resetEmail");
        assertThat(result2Return).isEqualTo("/register");

    }

    @Test
    void itShouldntSendResetPasswordEmail() throws Exception {
        //given
        String email = "janKowalski@gmial.com";
        ResetEmailRequest resetEmailRequest = new ResetEmailRequest(email);

        //when

        MvcResult result1 = mockMvc.perform(get("/reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        MvcResult result2 = mockMvc.perform(post("/reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .flashAttr("resetE", resetEmailRequest))
                .andReturn();



        int result1Status = result1.getResponse().getStatus();
        String result1Return = result1.getModelAndView().getViewName();

        String result2Return = result2.getResponse().getRedirectedUrl();

        //then
        assertThat(result1Status).isEqualTo(200);
        assertThat(result1Return).isEqualTo("reset/resetEmail");
        assertThat(result2Return).isEqualTo("/reset");

    }

    @Test
    void itShouldChangePassword() throws Exception {
        //given
        String email = "janKowalski@gmial.com";
        ResetRequest resetRequest = new ResetRequest(email, "changedPassword", "changedPassword");

        //when
        MvcResult resultSaveUser = mockMvc.perform(post("/rest/saveUser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(email))
                .andReturn();

        MvcResult resultSaveResetPasswordToken = mockMvc.perform(post("/rest/saveResetPasswordToken")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();


        MvcResult result1 = mockMvc.perform(get("/changePassword")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
                        .param("token", "passwordResetToken"))
                .andReturn();

        MvcResult result2 = mockMvc.perform(get("/resetPassword")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
        ).andReturn();

        MvcResult result3 = mockMvc.perform(post("/resetPassword")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
                .flashAttr("resetP", resetRequest)
                .param("email_attr", email)
        ).andReturn();

        MvcResult resultGetUser = mockMvc.perform(get("/rest/getUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf())
                .content("1")
        ).andReturn();


        int result2Status = result2.getResponse().getStatus();

        //then
        assertThat(result1.getFlashMap().toString()).contains("email_attr=janKowalski@gmial.com");
        assertThat(result1.getResponse().getRedirectedUrl()).isEqualTo("/resetPassword");

        assertThat(result2Status).isEqualTo(200);
        assertThat(result2.getModelAndView().getViewName()).isEqualTo("reset/resetPassword");

        System.out.println(resultGetUser.getResponse().getContentAsString());


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AppUserDeserialization confirmationToken = objectMapper.readValue(resultGetUser.getResponse().getContentAsString(), AppUserDeserialization.class);

        assertThat(BCrypt.checkpw("changedPassword", confirmationToken.getPassword())).isTrue();

    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
