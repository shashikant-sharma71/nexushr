package com.nexushr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AttendanceCorrectionRequest(
    LocalDateTime checkIn,
    LocalDateTime checkOut,
    @NotBlank @Size(max = 500) String reason
) {}
