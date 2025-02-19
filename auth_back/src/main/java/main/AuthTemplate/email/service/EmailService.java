package main.AuthTemplate.email.service;

public interface EmailService {
    void mailSend(String setFrom, String toMail, String title, String content);
    String joinEmail(String email);
    Boolean checkAuthNum(String email, String authNum);
}
