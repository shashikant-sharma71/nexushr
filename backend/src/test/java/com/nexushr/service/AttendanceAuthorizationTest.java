package com.nexushr.service;

import com.nexushr.controller.AttendanceController;
import com.nexushr.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceAuthorizationTest {
    @Test void attendanceAdministrationAndAuditRoutesRequireAdminOrHr() {
        PreAuthorize guard = AttendanceController.class.getAnnotation(PreAuthorize.class);
        assertNotNull(guard);
        assertEquals("hasAnyRole('ADMIN','HR')", guard.value());
    }

    @Test void userProvisioningIsNotPublicRegistration() throws Exception {
        PreAuthorize guard = AuthController.class.getMethod("register",
            com.nexushr.dto.AuthDtos.Register.class).getAnnotation(PreAuthorize.class);
        assertNotNull(guard);
        assertEquals("hasAnyRole('ADMIN','HR')", guard.value());
    }
}
