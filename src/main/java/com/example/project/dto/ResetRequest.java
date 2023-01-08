package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetRequest {

    public String email;
    public String password1;
    public String password2;
}
