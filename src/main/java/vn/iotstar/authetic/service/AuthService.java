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

    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();
        // Kiểm tra xem tài khoản đã được xác minh chưa
        if (!user.getIsVerified()) {
            return "Account is not verified. Please check your email for the OTP.";
        }

        // Kiểm tra mật khẩu (thực tế nên mã hóa mật khẩu và so sánh băm)
        if (!password.equals(user.getPassword())) {
            return "Invalid email or password!";
        }

        return "Login successful!";
    }

    public String forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();

        // Tạo mã OTP mới
        String otp = generateOtp();
        user.setOtpCode(otp);
        userRepository.save(user);

        // Gửi email OTP để đặt lại mật khẩu
        emailService.sendOtpEmail(email, "Password Reset OTP", otp);

        return "Password reset OTP sent to your email.";
    }

    public String resetPassword(String email, String otp, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();
        if (!otp.equals(user.getOtpCode())) {
            return "Invalid OTP!";
        }

        // Cập nhật mật khẩu (thực tế nên mã hóa mật khẩu)
        user.setPassword(newPassword);
        user.setOtpCode(null); // Xóa OTP sau khi sử dụng
        userRepository.save(user);

        return "Password reset successfully!";
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
