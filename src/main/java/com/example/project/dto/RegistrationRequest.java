package com.example.project.dto;

import com.example.project.validator.ValidPassword;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
