package com.cpmss.compound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing compound.
 *
 * <p>All fields are replaceable — the entire compound record is
 * overwritten with the values supplied here.
 *
 * @param compoundName display name of the compound
 * @param country      country where the compound is located
 * @param city         city where the compound is located
 * @param district     optional district or neighbourhood
 */
public record UpdateCompoundRequest(
        @NotBlank @Size(max = 100) String compoundName,
        @NotBlank @Size(max = 50) String country,
        @NotBlank @Size(max = 50) String city,
        @Size(max = 50) String district
) {}
