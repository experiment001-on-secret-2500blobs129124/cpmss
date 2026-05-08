package com.cpmss.hr.staffposition;

/**
 * Business rules for {@link StaffPosition} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see StaffPositionService
 */
public class StaffPositionRules {
    // No specific business rules beyond FK validation for StaffPosition.
    // Department existence is validated by the service layer via FK lookup.
}
