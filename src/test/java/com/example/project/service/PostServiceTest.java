package com.example.project.service;

import com.example.project.entity.AppUser;
import com.example.project.entity.AppUserRole;
import com.example.project.entity.Post;
import com.example.project.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private PostService underTest;

    @Mock
    private PostRepository postRepository;

    @BeforeEach
    public void beforeEach(){
        underTest = new PostService(postRepository);
    }

    @Test
    void itShouldReturnAllPosts() {
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

        given(postRepository.findAll()).willReturn(Arrays.asList(post));

        //when
        List<Post> result = underTest.getAllPosts();

        //then
        then(postRepository).should().findAll();
        assertThat(result.get(0).getContent()).isEqualTo("content");

    }

    @Test
    void itShouldReturnPostById() {
        //given
        Long id = 1L;
        String content = "content";

        AppUser user = new AppUser();
        user.setId(id);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("janKowalski@gmail.com");
        user.setPassword("password");
        user.setLocked(false);
        user.setEnabled(true);
        user.setAppUserRole(AppUserRole.USER);

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);

        given(postRepository.findById(id)).willReturn(Optional.of(post));
        //when
        Post result = underTest.getById(id);

        //then
        then(postRepository).should().findById(id);
        assertThat(result.getContent()).isEqualTo(content);

    }

    @Test
    void itShouldntReturnPostById() {
        //given
        Long id = 0L;

        given(postRepository.findById(any())).willReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTest.getById(id))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

    }
}