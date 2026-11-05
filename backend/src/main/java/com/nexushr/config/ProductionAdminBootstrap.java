package com.nexushr.config;

import com.nexushr.model.Role;
import com.nexushr.model.User;
import com.nexushr.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
@Profile("prod")
public class ProductionAdminBootstrap {
    @Bean
    CommandLineRunner bootstrapAdmin(
        UserRepository users,
        PasswordEncoder encoder,
        @Value("${BOOTSTRAP_ADMIN_EMAIL:}") String email,
        @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String password,
        @Value("${BOOTSTRAP_ADMIN_NAME:Platform Admin}") String name
    ) {
        return args -> {
            if (email.isBlank() && password.isBlank()) return;

            if (email.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                    "Set both BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD, or leave both empty"
                );
            }

            if (password.length() < 12) {
                throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_PASSWORD must contain at least 12 characters"
                );
            }

            // An existing administrator must never have their credentials overwritten.
            if (users.countByRole(Role.ADMIN) > 0) return;

            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

            // Never silently promote or overwrite an existing non-admin account.
            if (users.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
                throw new IllegalStateException(
                    "Bootstrap email already belongs to an existing user, but no ADMIN account exists. Resolve this account manually."
                );
            }

            users.save(new User(
                name.trim(),
                normalizedEmail,
                encoder.encode(password),
                Role.ADMIN
            ));
        };
    }
}
