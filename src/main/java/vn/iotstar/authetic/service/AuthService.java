package vn.iotstar.authetic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.authetic.entity.User;
import vn.iotstar.authetic.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public String registerUser(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            return "Email already exists!";
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // Thực tế nên mã hóa mật khẩu
        userRepository.save(user);

        String otp = generateOtp();
        user.setOtpCode(otp);
        userRepository.save(user);

        // Gửi email OTP
        emailService.sendOtpEmail(email, "Account Verification OTP", otp);

        return "User registered successfully! Check your email for the OTP.";
    }

    // Xác minh OTP
    public String verifyOtp(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();
        if (!otp.equals(user.getOtpCode())) {
            return "Invalid OTP!";
        }

        // OTP hợp lệ -> Kích hoạt tài khoản hoặc cho phép đặt lại mật khẩu
        user.setIsVerified(true);
        userRepository.save(user);

        return "OTP verified successfully!";
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
