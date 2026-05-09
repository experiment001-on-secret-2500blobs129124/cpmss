package com.cpmss.leasing.personresidesunder.dto;

import com.cpmss.leasing.common.HouseholdRelationship;
import com.cpmss.leasing.common.ResidencyPeriod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request payload for adding a resident under a contract.
 *
 * @param residentId            the person moving in
 * @param residencyPeriod       the move-in and optional move-out period
 * @param householdRelationship relationship to the primary contract holder
 */
public record AddPersonResidesUnderRequest(
        @NotNull UUID residentId,
        @NotNull @Valid ResidencyPeriod residencyPeriod,
        @NotNull HouseholdRelationship householdRelationship
) {}
