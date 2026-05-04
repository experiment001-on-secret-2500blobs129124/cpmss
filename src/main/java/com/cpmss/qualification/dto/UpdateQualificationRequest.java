package com.cpmss.qualification.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 * Request payload for updating an existing qualification.
 *
 * @param qualificationName the updated qualification name
 */
public record UpdateQualificationRequest(
        @NotBlank @Size(max = 100) String qualificationName
) {}
