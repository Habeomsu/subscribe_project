package main.AuthTemplate.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import main.AuthTemplate.config.RedisConfig;
import main.AuthTemplate.email.util.EmailUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisConfig redisConfig;

    private int authNumber; // 인증 번호를 클래스 변수로 저장

    @Value("${spring.mail.username}")
    private String serviceName;

    public EmailServiceImpl(JavaMailSender javaMailSender, RedisConfig redisConfig) {
        this.javaMailSender = javaMailSender;
        this.redisConfig = redisConfig;
    }

    /* 랜덤 인증번호 생성 */
    private void makeRandomNum() {
        EmailUtils emailUtils = new EmailUtils();
        this.authNumber = emailUtils.makeRandomNum(); // 인증 번호 생성 후 클래스 변수에 저장
    }

    /* 이메일 전송 */
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom); // 서비스 이름
            helper.setTo(toMail); // 고객 이메일
            helper.setSubject(title); // 이메일 제목
            helper.setText(content, true); // 내용, HTML: true
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // 에러 출력
        }

        // redis에 3분 동안 이메일과 인증 코드 저장
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        valOperations.set(toMail, Integer.toString(authNumber), 180, TimeUnit.SECONDS);
    }

    /* 이메일 작성 */
    @Override
    public String joinEmail(String email) {
        makeRandomNum(); // 인증 번호 생성
        String customerMail = email;
        String title = "회원 가입을 위한 이메일입니다!";
        String content =
                "이메일을 인증하기 위한 절차입니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "회원 가입 폼에 해당 번호를 입력해주세요.";
        mailSend(serviceName, customerMail, title, content); // 이메일 전송
        return Integer.toString(authNumber); // 인증 번호 반환
    }

    @Override
    public Boolean checkAuthNum(String email, String authNum) {
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String code = valOperations.get(email);
        log.info(authNum);
        if (Objects.equals(code, authNum)) {
            return true;
        } else return false;
    }
}
