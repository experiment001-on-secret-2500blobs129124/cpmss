package com.cpmss.personresidesunder.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response payload for a person-resides-under record.
 *
 * @param residentId            the resident person UUID
 * @param contractId            the contract UUID
 * @param moveInDate            the move-in date
 * @param moveOutDate           the move-out date (null = still residing)
 * @param householdRelationship relationship to the primary contract holder
 */
public record PersonResidesUnderResponse(
        UUID residentId,
        UUID contractId,
        LocalDate moveInDate,
        LocalDate moveOutDate,
        String householdRelationship
) {}
