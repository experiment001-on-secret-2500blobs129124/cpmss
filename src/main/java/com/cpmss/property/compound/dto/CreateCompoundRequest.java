package com.cpmss.property.compound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a compound.
 *
 * <p>All required fields map directly to the {@code Compound} table
 * columns. District is the only optional field.
 *
 * @param compoundName display name of the compound
 * @param country      country where the compound is located
 * @param city         city where the compound is located
 * @param district     optional district or neighbourhood
 */
public record CreateCompoundRequest(
        @NotBlank @Size(max = 100) String compoundName,
        @NotBlank @Size(max = 50) String country,
        @NotBlank @Size(max = 50) String city,
        @Size(max = 50) String district
) {}
