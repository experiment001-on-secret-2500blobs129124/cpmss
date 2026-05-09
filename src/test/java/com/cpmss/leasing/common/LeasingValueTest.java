package com.cpmss.leasing.common;

import com.cpmss.finance.money.Money;
import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeasingValueTest {

    @Test
    void acceptsContractPeriodsWithEndAfterStart() {
        ContractPeriod period = new ContractPeriod(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 6, 1));

        assertThat(period.getStartDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(period.getEndDate()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    void rejectsContractPeriodsWithoutStrictlyLaterEndDate() {
        assertThatThrownBy(() -> new ContractPeriod(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 1)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Contract end date must be after start date");
    }

    @Test
    void acceptsResidencyPeriodsWithEndAfterMoveIn() {
        ResidencyPeriod period = new ResidencyPeriod(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 8));

        assertThat(period.moveInDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(period.moveOutDate()).isEqualTo(LocalDate.of(2026, 5, 8));
    }

    @Test
    void rejectsResidencyPeriodsWithoutStrictlyLaterMoveOutDate() {
        assertThatThrownBy(() -> new ResidencyPeriod(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 1)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Move-out date must be after move-in date");
    }

    @Test
    void parsesContractVocabularyLabels() {
        assertThat(ContractType.fromLabel("Residential")).isEqualTo(ContractType.RESIDENTIAL);
        assertThat(ContractType.fromLabel("Commercial")).isEqualTo(ContractType.COMMERCIAL);
        assertThat(ContractStatus.fromLabel("Active")).isEqualTo(ContractStatus.ACTIVE);
        assertThat(ContractStatus.fromLabel("Renewed")).isEqualTo(ContractStatus.RENEWED);
    }

    @Test
    void rejectsUnknownContractVocabularyLabels() {
        assertThatThrownBy(() -> ContractType.fromLabel("Parking"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Contract type must be one of: Residential, Commercial");
        assertThatThrownBy(() -> ContractStatus.fromLabel("Closed"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Contract status must be one of: Draft, Active, Expired, Terminated, Renewed");
    }

    @Test
    void parsesPartyAndHouseholdLabels() {
        assertThat(ContractPartyRole.fromLabel("Primary Signer"))
                .isEqualTo(ContractPartyRole.PRIMARY_SIGNER);
        assertThat(ContractPartyRole.fromLabel("Corporate Representative"))
                .isEqualTo(ContractPartyRole.CORPORATE_REPRESENTATIVE);
        assertThat(HouseholdRelationship.fromLabel("Co-tenant"))
                .isEqualTo(HouseholdRelationship.CO_TENANT);
        assertThat(HouseholdRelationship.fromLabel("Child"))
                .isEqualTo(HouseholdRelationship.CHILD);
    }

    @Test
    void parsesInstallmentVocabularyLabels() {
        assertThat(InstallmentType.fromLabel("Rent")).isEqualTo(InstallmentType.RENT);
        assertThat(InstallmentType.fromLabel("Penalty")).isEqualTo(InstallmentType.PENALTY);
        assertThat(InstallmentStatus.fromLabel("Partially Paid"))
                .isEqualTo(InstallmentStatus.PARTIALLY_PAID);
        assertThat(InstallmentStatus.fromLabel("Cancelled"))
                .isEqualTo(InstallmentStatus.CANCELLED);
    }

    @Test
    void enforcesInstallmentStatusTransitions() {
        assertThat(InstallmentStatus.PENDING.canTransitionTo(InstallmentStatus.PAID)).isTrue();
        assertThat(InstallmentStatus.OVERDUE.canTransitionTo(InstallmentStatus.PARTIALLY_PAID)).isTrue();
        assertThat(InstallmentStatus.PAID.canTransitionTo(InstallmentStatus.OVERDUE)).isFalse();
        assertThat(InstallmentStatus.CANCELLED.canTransitionTo(InstallmentStatus.PENDING)).isFalse();
    }

    @Test
    void leasingMoneyUsesExplicitCurrency() {
        Money rent = Money.positive(new BigDecimal("1250.00"), "egp");

        assertThat(rent.getAmount()).isEqualByComparingTo("1250.00");
        assertThat(rent.getCurrency()).isEqualTo("EGP");
    }

    @Test
    void convertersPreserveDatabaseLabels() {
        assertThat(new ContractTypeConverter().convertToDatabaseColumn(ContractType.COMMERCIAL))
                .isEqualTo("Commercial");
        assertThat(new ContractStatusConverter().convertToDatabaseColumn(ContractStatus.TERMINATED))
                .isEqualTo("Terminated");
        assertThat(new ContractPartyRoleConverter()
                .convertToDatabaseColumn(ContractPartyRole.AUTHORIZING_STAFF))
                .isEqualTo("Authorizing Staff");
        assertThat(new HouseholdRelationshipConverter()
                .convertToDatabaseColumn(HouseholdRelationship.GUEST))
                .isEqualTo("Guest");
        assertThat(new InstallmentTypeConverter().convertToDatabaseColumn(InstallmentType.DEPOSIT))
                .isEqualTo("Deposit");
        assertThat(new InstallmentStatusConverter().convertToDatabaseColumn(InstallmentStatus.OVERDUE))
                .isEqualTo("Overdue");
    }
}
