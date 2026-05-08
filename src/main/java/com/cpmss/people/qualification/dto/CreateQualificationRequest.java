package com.cpmss.people.qualification.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * Request payload for creating a new qualification.
 *
 * @param qualificationName the qualification's name
 */
public record CreateQualificationRequest(
        @NotBlank @Size(max = 100) String qualificationName
) {}
