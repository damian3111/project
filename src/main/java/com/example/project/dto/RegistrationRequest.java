package com.example.project.dto;

import com.example.project.validator.ValidPassword;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {

    @Size(min = 3, message = "First name should have at least 3 characters")
    public String firstName;
    @Size(min = 3, message = "Last name should have at least 3 characters")
    public String lastName;
    public String email;
    @ValidPassword
    public String password1;
    public String password2;
}
