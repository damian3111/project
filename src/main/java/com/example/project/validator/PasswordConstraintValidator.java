package com.example.project.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.passay.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(final ValidPassword arg0) {
    }
    @SneakyThrows
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        Properties props = new Properties();

        InputStream inputStream = getClass()

                .getClassLoader().getResourceAsStream("passay.properties");

        props.load(inputStream);

        MessageResolver resolver = new PropertiesMessageResolver(props);

        PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
                new LengthRule(8, 16),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule(),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),

                // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 12345)

                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()

                .disableDefaultConstraintViolation();
        return false;
    }
}

