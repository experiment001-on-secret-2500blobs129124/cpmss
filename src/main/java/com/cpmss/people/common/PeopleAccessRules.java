package com.cpmss.people.common;

import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;

import java.util.UUID;

/**
 * Service-level authorization rules for person, role, and qualification data.
 */
public class PeopleAccessRules {

    /**
     * Requires authority to maintain people catalog records.
     *
     * @param user current authenticated user
     */
    public void requireHrAuthority(CurrentUser user) {
        if (isHrAuthority(user)) {
            return;
        }
        throw new ApiException(PeopleErrorCode.PEOPLE_RECORD_ACCESS_DENIED);
    }

    /**
     * Allows HR/security readers or the linked person's own row.
     *
     * @param user     current authenticated user
     * @param personId person UUID being read
     */
    public void requireCanViewPerson(CurrentUser user, UUID personId) {
        if (isPersonReader(user) || isOwnPerson(user, personId)) {
            return;
        }
        throw new ApiException(PeopleErrorCode.PEOPLE_RECORD_ACCESS_DENIED);
    }

    /**
     * Requires authority to update person records.
     *
     * <p>HR/security roles may maintain person rows broadly. Linked users may
     * update only their own contact fields when the service confirms the
     * request is contact-only.
     *
     * @param user              current authenticated user
     * @param personId          person UUID being updated
     * @param contactOnlyUpdate true when only phones/emails change
     */
    public void requireCanUpdatePerson(CurrentUser user, UUID personId,
                                       boolean contactOnlyUpdate) {
        if (isHrAuthority(user)
                || user.hasRole(SystemRole.SECURITY_OFFICER)
                || (contactOnlyUpdate && isOwnPerson(user, personId))) {
            return;
        }
        throw new ApiException(PeopleErrorCode.PEOPLE_RECORD_ACCESS_DENIED);
    }

    private boolean isHrAuthority(CurrentUser user) {
        return user.hasRole(SystemRole.ADMIN)
                || user.hasRole(SystemRole.GENERAL_MANAGER)
                || user.hasRole(SystemRole.HR_OFFICER);
    }

    private boolean isPersonReader(CurrentUser user) {
        return isHrAuthority(user) || user.hasRole(SystemRole.SECURITY_OFFICER);
    }

    private boolean isOwnPerson(CurrentUser user, UUID personId) {
        return user.personId() != null && user.personId().equals(personId);
    }
}
