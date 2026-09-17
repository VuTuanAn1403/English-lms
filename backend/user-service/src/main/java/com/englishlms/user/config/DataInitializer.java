package com.englishlms.user.config;

import com.englishlms.user.entity.Role;
import com.englishlms.user.entity.User;
import com.englishlms.user.entity.UserStatus;
import com.englishlms.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            initUser("admin@gmail.com", "Administrator", "123456", Role.ADMIN, "550e8400-e29b-41d4-a716-446655440000");
            initUser("student@gmail.com", "Nguyễn Văn A", "123456", Role.STUDENT, "550e8400-e29b-41d4-a716-446655440001");
        } catch (Exception e) {
            log.warn("DataInitializer skipped initialization: {}", e.getMessage());
        }
    }

    private void initUser(String email, String fullName, String rawPassword, Role role, String uuidStr) {
        if (!userRepository.existsByEmail(email)) {
            log.info("Creating seed user: {}", email);
            User user = User.builder()
                    .id(UUID.fromString(uuidStr))
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .status(UserStatus.ACTIVE)
                    .avatar("https://ui-avatars.com/api/?name=" + role.name())
                    .build();
            userRepository.save(user);
        } else {
            log.info("User {} already exists in database.", email);
        }
    }
}
