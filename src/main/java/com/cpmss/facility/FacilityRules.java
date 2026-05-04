package com.cpmss.facility;

import com.cpmss.exception.BusinessException;

/**
 * Business rules for {@link Facility} operations.
 *
 * <p>Stateless — all data is loaded by the service and passed in.
 *
 * @see FacilityService
 */
public class FacilityRules {

    /**
     * Validates the management type and company FK consistency.
     *
     * <p>When {@code managementType} is "Vendor", a non-null company
     * ID is required. When "Compound", the company ID must be null.
     *
     * @param managementType     "Compound" or "Vendor"
     * @param managedByCompanyId the managing company UUID (may be {@code null})
     * @throws BusinessException if the combination is invalid
     */
    public void validateManagementType(String managementType, java.util.UUID managedByCompanyId) {
        if ("Vendor".equalsIgnoreCase(managementType) && managedByCompanyId == null) {
            throw new BusinessException(
                    "A managing company is required when management type is 'Vendor'");
        }
        if ("Compound".equalsIgnoreCase(managementType) && managedByCompanyId != null) {
            throw new BusinessException(
                    "A managing company must not be set when management type is 'Compound'");
        }
    }
}
