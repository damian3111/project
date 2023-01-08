package com.example.project.email;

public interface EmailSender {

    void send(String to, String email) throws Exception;
}
