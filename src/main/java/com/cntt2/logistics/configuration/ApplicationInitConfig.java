package com.cntt2.logistics.configuration;

import com.cntt2.logistics.entity.Role;
import com.cntt2.logistics.entity.User;
import com.cntt2.logistics.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User user = User.builder()
                        .fullName("Administrator")
                        .email("admin@gmail.com")
                        .phone("")
                        .birthday(LocalDate.of(2000, 1, 1))
                        .password(passwordEncoder.encode("admin"))
                        .province("")
                        .district("")
                        .ward("")
                        .address("")
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .createdBy("system")
                        .build();

                userRepository.save(user);
                log.info("Admin user created successfully");
            }
        };
    }
}
