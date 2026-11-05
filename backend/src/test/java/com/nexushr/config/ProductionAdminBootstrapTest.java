package com.nexushr.config;

import com.nexushr.model.Role;
import com.nexushr.model.User;
import com.nexushr.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionAdminBootstrapTest {

    @Mock UserRepository users;
    @Mock PasswordEncoder encoder;

    private CommandLineRunner runner(String email, String password, String name) {
        return new ProductionAdminBootstrap().bootstrapAdmin(
            users, encoder, email, password, name
        );
    }

    @Test
    void createsAdminWhenDatabaseHasNoAdmin() throws Exception {
        when(users.countByRole(Role.ADMIN)).thenReturn(0L);
        when(users.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("StrongPassword123")).thenReturn("hashed-password");

        runner(" Admin@Example.com ", "StrongPassword123", " Platform Admin ").run(new String[0]);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());

        User saved = captor.getValue();
        assertEquals(Role.ADMIN, saved.role);
        assertEquals("admin@example.com", saved.email);
        assertEquals("hashed-password", saved.password);
        assertEquals("Platform Admin", saved.name);
    }

    @Test
    void doesNotCreateDuplicateWhenAdminAlreadyExists() throws Exception {
        when(users.countByRole(Role.ADMIN)).thenReturn(1L);

        runner("admin@example.com", "StrongPassword123", "Admin").run(new String[0]);

        verify(users, never()).save(any());
        verify(users, never()).findByEmailIgnoreCase(anyString());
        verifyNoInteractions(encoder);
    }

    @Test
    void failsIfBootstrapEmailBelongsToNonAdminAndNoAdminExists() {
        User existing = new User("Employee", "admin@example.com", "existing-hash", Role.EMPLOYEE);
        when(users.countByRole(Role.ADMIN)).thenReturn(0L);
        when(users.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(existing));

        CommandLineRunner runner = runner(
            "admin@example.com", "StrongPassword123", "Admin"
        );

        assertThrows(IllegalStateException.class, () -> runner.run(new String[0]));
        assertEquals(Role.EMPLOYEE, existing.role);
        assertEquals("existing-hash", existing.password);
        verify(users, never()).save(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void skipsBootstrapWhenBothCredentialsAreEmpty() throws Exception {
        runner("", "", "Admin").run(new String[0]);

        verifyNoInteractions(users, encoder);
    }

    @Test
    void rejectsWhenOnlyOneBootstrapCredentialIsProvided() {
        CommandLineRunner runner = runner("admin@example.com", "", "Admin");

        assertThrows(IllegalStateException.class, () -> runner.run(new String[0]));
        verifyNoInteractions(users, encoder);
    }

    @Test
    void rejectsBootstrapPasswordShorterThanTwelveCharacters() {
        CommandLineRunner runner = runner("admin@example.com", "short", "Admin");

        assertThrows(IllegalStateException.class, () -> runner.run(new String[0]));
        verifyNoInteractions(users, encoder);
    }
}
