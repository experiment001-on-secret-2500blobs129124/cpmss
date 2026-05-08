package com.cpmss.property.facility;

import com.cpmss.platform.exception.BusinessException;
import com.cpmss.property.common.FacilityManagementType;

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
     * <p>When {@code managementType} is Vendor, a non-null company ID is
     * required. When Compound, the company ID must be null.
     *
     * @param managementType     the facility management type
     * @param managedByCompanyId the managing company UUID (may be {@code null})
     * @throws BusinessException if the combination is invalid
     */
    public void validateManagementType(FacilityManagementType managementType, java.util.UUID managedByCompanyId) {
        if (managementType == null) {
            throw new BusinessException("Facility management type is required");
        }
        if (managementType == FacilityManagementType.VENDOR && managedByCompanyId == null) {
            throw new BusinessException(
                    "A managing company is required when management type is 'Vendor'");
        }
        if (managementType == FacilityManagementType.COMPOUND && managedByCompanyId != null) {
            throw new BusinessException(
                    "A managing company must not be set when management type is 'Compound'");
        }
    }
}
