package com.example.backend.mailcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSendService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSendService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.from-address}") // 설정 파일에서 '보내는 사람' 주소를 가져옵니다.
    private String fromAddress;


    public void sendVerificationEmail(String toEmail, String verificationCode) {
            SimpleMailMessage message = new SimpleMailMessage();

            // 보내는 사람 이메일 주소 (설정 파일에서 가져옴)
            message.setFrom(fromAddress); 
            message.setTo(toEmail);
            message.setSubject("[쉼, 한국] 회원가입 이메일 인증 코드입니다.");
            
            String emailContent = "안녕하세요.\n\n"
                                + "요청하신 회원가입 인증 코드입니다.\n"
                                + "아래 코드를 회원가입 화면에 입력해주세요.\n\n"
                                + "인증 코드: " + verificationCode + "\n\n"
                                + "감사합니다.";

            message.setText(emailContent);
            
            mailSender.send(message);
        }
}