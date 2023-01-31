package com.example.project.dto;

import com.example.project.entity.AppUserRole;
import com.example.project.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
public class AppUserDeserialization {


    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private AppUserRole appUserRole;
    private Boolean locked;
    private Boolean enabled;
    private List<Post> post;
    private List<Object> authorities;

    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
}
