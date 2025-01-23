package vn.iotstar.authetic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String subject, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Your OTP code is: " + otp);

        // Gửi email
        try {
            mailSender.send(message);
            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }

    }
}
