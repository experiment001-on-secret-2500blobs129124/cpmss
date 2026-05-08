package com.cpmss.leasing.personresidesunder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for adding a resident under a contract.
 *
 * @param residentId            the person moving in
 * @param moveInDate            the move-in date
 * @param householdRelationship relationship to the primary contract holder
 */
public record AddPersonResidesUnderRequest(
        @NotNull UUID residentId,
        @NotNull LocalDate moveInDate,
        @NotBlank String householdRelationship
) {}
