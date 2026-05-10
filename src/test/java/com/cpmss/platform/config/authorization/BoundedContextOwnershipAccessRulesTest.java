package com.cpmss.platform.config.authorization;

import com.cpmss.finance.bankaccount.BankAccount;
import com.cpmss.finance.common.FinanceAccessRules;
import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.identity.auth.AppUserAccessRules;
import com.cpmss.identity.auth.CurrentUser;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.leasing.common.LeasingAccessRules;
import com.cpmss.maintenance.common.MaintenanceAccessRules;
import com.cpmss.organization.common.DepartmentScopeService;
import com.cpmss.organization.common.OrganizationAccessRules;
import com.cpmss.people.common.PeopleAccessRules;
import com.cpmss.people.person.Person;
import com.cpmss.performance.common.PerformanceAccessRules;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.property.common.PropertyAccessRules;
import com.cpmss.workforce.common.WorkforceAccessRules;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Protects service-owned authorization rules across bounded contexts.
 */
class BoundedContextOwnershipAccessRulesTest {

    @Test
    void hrProfileReadAllowsOwnPersonButRejectsOtherStaff() {
        UUID ownPersonId = UUID.randomUUID();
        UUID otherPersonId = UUID.randomUUID();
        CurrentUser staff = user(SystemRole.STAFF, ownPersonId);
        HrAccessRules rules = new HrAccessRules();

        assertThatCode(() -> rules.requireCanViewStaffProfile(staff, ownPersonId))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.requireCanViewStaffProfile(staff, otherPersonId))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void hrApplicationSubmitAllowsApplicantOnlyForOwnPerson() {
        UUID applicantId = UUID.randomUUID();
        HrAccessRules rules = new HrAccessRules();

        assertThatCode(() -> rules.requireCanSubmitApplication(
                user(SystemRole.APPLICANT, applicantId), applicantId))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.requireCanSubmitApplication(
                user(SystemRole.APPLICANT, UUID.randomUUID()), applicantId))
                .isInstanceOf(ApiException.class);
        assertThatCode(() -> rules.requireCanSubmitApplication(
                user(SystemRole.HR_OFFICER, UUID.randomUUID()), applicantId))
                .doesNotThrowAnyException();
    }

    @Test
    void workforceDepartmentManagersNeedOwnedDepartmentScope() {
        UUID departmentId = UUID.randomUUID();
        CurrentUser manager = user(SystemRole.DEPARTMENT_MANAGER, UUID.randomUUID());
        DepartmentScopeService scopeService = mock(DepartmentScopeService.class);
        WorkforceAccessRules rules = new WorkforceAccessRules();

        when(scopeService.isBusinessAdmin(manager)).thenReturn(false);
        when(scopeService.managesDepartment(manager, departmentId)).thenReturn(false);

        assertThatThrownBy(() -> rules.requireCanManageDepartment(
                manager, departmentId, scopeService))
                .isInstanceOf(ApiException.class);

        when(scopeService.managesDepartment(manager, departmentId)).thenReturn(true);
        assertThatCode(() -> rules.requireCanManageDepartment(
                manager, departmentId, scopeService))
                .doesNotThrowAnyException();
    }

    @Test
    void performanceDepartmentManagerReviewMustBeSelfAuthoredInOwnedDepartment() {
        UUID reviewerId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        CurrentUser manager = user(SystemRole.DEPARTMENT_MANAGER, reviewerId);
        DepartmentScopeService scopeService = mock(DepartmentScopeService.class);
        PerformanceAccessRules rules = new PerformanceAccessRules();

        when(scopeService.managesDepartment(manager, departmentId)).thenReturn(true);

        assertThatCode(() -> rules.requireCanCreateReview(
                manager, reviewerId, departmentId, scopeService))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.requireCanCreateReview(
                manager, UUID.randomUUID(), departmentId, scopeService))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void financeBankAccountReadAllowsOnlyFinanceOrPersonOwner() {
        UUID ownerId = UUID.randomUUID();
        Person owner = new Person();
        owner.setId(ownerId);
        BankAccount account = new BankAccount();
        account.setAccountOwner(owner);

        FinanceAccessRules rules = new FinanceAccessRules();

        assertThatCode(() -> rules.requireCanViewBankAccount(
                user(SystemRole.STAFF, ownerId), account))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.requireCanViewBankAccount(
                user(SystemRole.STAFF, UUID.randomUUID()), account))
                .isInstanceOf(ApiException.class);
        assertThatCode(() -> rules.requireFinanceAuthority(
                user(SystemRole.ACCOUNTANT, UUID.randomUUID())))
                .doesNotThrowAnyException();
    }

    @Test
    void peopleRowsAllowOwnReadButReserveMutationToHrOrSecurity() {
        UUID personId = UUID.randomUUID();
        PeopleAccessRules rules = new PeopleAccessRules();

        assertThatCode(() -> rules.requireCanViewPerson(
                user(SystemRole.STAFF, personId), personId))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.requireCanUpdatePerson(
                user(SystemRole.STAFF, personId)))
                .isInstanceOf(ApiException.class);
        assertThatCode(() -> rules.requireCanUpdatePerson(
                user(SystemRole.SECURITY_OFFICER, UUID.randomUUID())))
                .doesNotThrowAnyException();
    }

    @Test
    void organizationAllowsDepartmentManagerOnlyForAssignedDepartmentRead() {
        UUID departmentId = UUID.randomUUID();
        CurrentUser manager = user(SystemRole.DEPARTMENT_MANAGER, UUID.randomUUID());
        DepartmentScopeService scopeService = mock(DepartmentScopeService.class);
        OrganizationAccessRules rules = new OrganizationAccessRules();

        when(scopeService.managesDepartment(manager, departmentId)).thenReturn(false);
        assertThatThrownBy(() -> rules.requireCanViewDepartment(
                manager, departmentId, scopeService))
                .isInstanceOf(ApiException.class);

        when(scopeService.managesDepartment(manager, departmentId)).thenReturn(true);
        assertThatCode(() -> rules.requireCanViewDepartment(
                manager, departmentId, scopeService))
                .doesNotThrowAnyException();
    }

    @Test
    void facilityContextsSeparateReadFinanceFromFacilityMutation() {
        PropertyAccessRules propertyRules = new PropertyAccessRules();
        MaintenanceAccessRules maintenanceRules = new MaintenanceAccessRules();
        CurrentUser accountant = user(SystemRole.ACCOUNTANT, UUID.randomUUID());
        CurrentUser facilityOfficer = user(SystemRole.FACILITY_OFFICER, UUID.randomUUID());

        assertThatCode(() -> propertyRules.requirePropertyReader(accountant))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> propertyRules.requirePropertyAdministrator(accountant))
                .isInstanceOf(ApiException.class);
        assertThatCode(() -> maintenanceRules.requireMaintenanceAdministrator(facilityOfficer))
                .doesNotThrowAnyException();
    }

    @Test
    void leasingAndIdentityReserveBroadActionsForOwningAdminRoles() {
        LeasingAccessRules leasingRules = new LeasingAccessRules();
        AppUserAccessRules accountRules = new AppUserAccessRules();
        UUID accountId = UUID.randomUUID();
        CurrentUser staff = new CurrentUser(
                accountId, UUID.randomUUID(), SystemRole.STAFF, "staff@example.com");

        assertThatThrownBy(() -> leasingRules.requireLeasingAuthority(staff))
                .isInstanceOf(ApiException.class);
        assertThatCode(() -> accountRules.requireCanViewAccount(staff, accountId))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> accountRules.requireAccountManager(staff))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void identityAllowsDepartmentManagersToCreateOnlyLowestLevelAccounts() {
        AppUserAccessRules accountRules = new AppUserAccessRules();
        CurrentUser manager = user(SystemRole.DEPARTMENT_MANAGER, UUID.randomUUID());

        assertThatCode(() -> accountRules.requireAccountCreator(manager))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> accountRules.requireAccountManager(manager))
                .isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> accountRules.requireAccountCreator(
                user(SystemRole.STAFF, UUID.randomUUID())))
                .isInstanceOf(ApiException.class);
    }

    private CurrentUser user(SystemRole role, UUID personId) {
        return new CurrentUser(UUID.randomUUID(), personId, role, role.name() + "@example.com");
    }
}
