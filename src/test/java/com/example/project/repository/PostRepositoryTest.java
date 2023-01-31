package com.example.project.repository;

import com.example.project.entity.AppUser;
import com.example.project.entity.AppUserRole;
import com.example.project.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Test
    void itShouldSetPostContent() {
        //given
        AppUser user = new AppUser();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("janKowalski@gmail.com");
        user.setPassword("password");
        user.setLocked(false);
        user.setEnabled(true);
        user.setAppUserRole(AppUserRole.USER);

        Post post = new Post();
        post.setUser(user);
        post.setContent("content");

        //when
        userRepository.save(user);
        underTest.save(post);

        int result = underTest.setContentById(user.getId(), "changed content");

        Optional<Post> changedPost = underTest.findById(user.getId());
        //then

        assertThat(result).isEqualTo(1);

        assertThat(changedPost)
                .isPresent()
                .hasValueSatisfying(a -> assertThat(a.getContent()).isEqualTo("changed content"));




    }
}