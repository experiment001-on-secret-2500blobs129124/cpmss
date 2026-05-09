package com.cpmss.leasing.personresidesunder.dto;

import com.cpmss.leasing.common.HouseholdRelationship;
import com.cpmss.leasing.common.ResidencyPeriod;

import java.util.UUID;

/**
 * Response payload for a person-resides-under record.
 *
 * @param residentId            the resident person UUID
 * @param contractId            the contract UUID
 * @param residencyPeriod       the move-in and optional move-out period
 * @param householdRelationship relationship to the primary contract holder
 */
public record PersonResidesUnderResponse(
        UUID residentId,
        UUID contractId,
        ResidencyPeriod residencyPeriod,
        HouseholdRelationship householdRelationship
) {}
