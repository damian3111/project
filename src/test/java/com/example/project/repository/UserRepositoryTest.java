package com.example.project.repository;

import com.example.project.entity.AppUser;
import com.example.project.entity.AppUserRole;
import com.sun.mail.imap.AppendUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Test
    void itShouldFindUserByEmail() {
        //given
        String email = "jankowalski@gmail.com";
        AppUser user = new AppUser("Jan", "Kowalski", "jankowalski@gmail.com", "password", AppUserRole.USER);

        underTest.save(user);

        //when
        Optional<AppUser> result = underTest.findByEmail(email);

        //then

        assertThat(result)
                .isPresent();

    }


    @Test
    void itShouldUnableUsesAccount() {
//given
        AppUser user = new AppUser("Jan", "Kowalski", "jankowalski@gmail.com", "password", AppUserRole.USER);
        user.setEnabled(false);

        underTest.save(user);
        //when

        int result = underTest.updateEnabled(user.getId());
        Optional<AppUser> user2 = underTest.findByEmail(user.getEmail());

        //then
        assertThat(result).isEqualTo(1);
        assertThat(user2)
                .hasValueSatisfying(a -> assertThat(a.getEnabled()).isEqualTo(true));

    }

    @Test
    void itShouldChangeUserPassword() {
        //given
        String email = "jankowalski@gmail.com";
        String password = "password";

        AppUser user = new AppUser("Jan", "Kowalski", email, password, AppUserRole.USER);

        underTest.save(user);
        //when
        int result = underTest.changePassword(email, "pass");

        //then
        Optional<AppUser> byEmail = underTest.findByEmail(email);


        assertThat(result).isEqualTo(1);
        assertThat(byEmail.get().getPassword()).isEqualTo("pass");

    }
}