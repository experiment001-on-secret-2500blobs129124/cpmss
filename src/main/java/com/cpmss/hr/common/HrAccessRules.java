package com.cpmss.hr.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for HR records and workflows.
 *
 * <p>Route guards decide whether an endpoint can be called. These rules keep
 * HR record scope close to the services that load and mutate HR data.
 */
public class HrAccessRules {

    /**
     * Requires HR administration authority.
     *
     * @param user current authenticated user
     * @throws ApiException if the user is not allowed to administer HR data
     */
    public void requireHrAdministrator(CurrentUser user) {
        if (!isHrAdministrator(user)) {
            throw new ApiException(HrErrorCode.HR_RECORD_ACCESS_DENIED);
        }
    }

    /**
     * Allows broad HR readers or the linked staff member's own profile.
     *
     * @param user    current authenticated user
     * @param staffId staff person UUID being read
     * @throws ApiException if the user cannot read this staff profile
     */
    public void requireCanViewStaffProfile(CurrentUser user, UUID staffId) {
        if (isHrAdministrator(user) || isOwnPerson(user, staffId)) {
            return;
        }
        throw new ApiException(HrErrorCode.HR_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows HR administrators to submit/import any application and applicants
     * to submit applications only for their own linked person record.
     *
     * @param user        current authenticated user
     * @param applicantId person UUID on the application payload
     * @throws ApiException if the user cannot submit for this applicant
     */
    public void requireCanSubmitApplication(CurrentUser user, UUID applicantId) {
        if (isHrAdministrator(user)
                || (user.hasRole(SystemRole.APPLICANT) && isOwnPerson(user, applicantId))) {
            return;
        }
        throw new ApiException(HrErrorCode.HR_RECORD_ACCESS_DENIED);
    }


    /**
     * Allows HR administrators or the owning applicant to read application data.
     *
     * @param user        current authenticated user
     * @param applicantId person UUID on the application
     * @throws ApiException if the user cannot read this application
     */
    public void requireCanViewApplication(CurrentUser user, UUID applicantId) {
        if (isHrAdministrator(user)
                || (user.hasRole(SystemRole.APPLICANT) && isOwnPerson(user, applicantId))) {
            return;
        }
        throw new ApiException(HrErrorCode.HR_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows HR administrators or the owning applicant to upload/replace a CV.
     *
     * @param user        current authenticated user
     * @param applicantId person UUID on the application
     * @throws ApiException if the user cannot modify this application CV
     */
    public void requireCanUploadApplicationCv(CurrentUser user, UUID applicantId) {
        requireCanViewApplication(user, applicantId);
    }

    /**
     * Checks whether the role has full HR authority.
     *
     * @param user current authenticated user
     * @return true for ADMIN, GENERAL_MANAGER, and HR_OFFICER
     */
    public boolean isHrAdministrator(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.HR_OFFICER);
    }

    private boolean isOwnPerson(CurrentUser user, UUID personId) {
        return user.personId() != null && user.personId().equals(personId);
    }
}
