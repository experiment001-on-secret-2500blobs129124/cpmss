package com.cpmss.security.accesspermit;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.leasing.contract.Contract;
import com.cpmss.people.person.Person;
import com.cpmss.hr.staffprofile.StaffProfile;
import com.cpmss.maintenance.workorder.WorkOrder;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Owned entity representing an access permit (physical card/pass) issued to a person.
 *
 * <p>Each permit must reference exactly one entitlement basis:
 * Staff Badge → {@code staff_profile_id} |
 * Resident Card → {@code contract_id} |
 * Contractor Pass → {@code work_order_id} |
 * Visitor Pass → {@code invited_by_id}.
 * Mutual exclusion is enforced in {@link AccessPermitRules} and
 * by a CHECK constraint in V2.
 *
 * <p>Permits are revoked by status change, never deleted.
 */
@Entity
@Table(name = "Access_Permit")
@AttributeOverride(name = "id", column = @Column(name = "permit_id", nullable = false))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessPermit extends BaseEntity {

    /** System-unique permit number. */
    @Column(name = "permit_no", nullable = false, unique = true, length = 20)
    private String permitNo;

    /** Permit type (Staff Badge, Resident Card, Visitor Pass, Contractor Pass, Vehicle Sticker). */
    @Convert(converter = PermitTypeConverter.class)
    @Column(name = "permit_type", nullable = false, length = 50)
    @Setter(lombok.AccessLevel.NONE)
    private PermitType permitType;

    /** Access level (Full Access, Restricted Areas, Common Areas Only). */
    @Convert(converter = AccessLevelConverter.class)
    @Column(name = "access_level", length = 50)
    @Setter(lombok.AccessLevel.NONE)
    private AccessLevel accessLevel;

    /** Lifecycle status (Active, Suspended, Revoked, Expired). */
    @Convert(converter = PermitStatusConverter.class)
    @Column(name = "permit_status", nullable = false, length = 50)
    @Setter(lombok.AccessLevel.NONE)
    private PermitStatus permitStatus;

    /** Permit issue and expiry dates mapped to the existing date columns. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "issueDate",
                    column = @Column(name = "issue_date", nullable = false)),
            @AttributeOverride(name = "expiryDate",
                    column = @Column(name = "expiry_date"))
    })
    @Setter(lombok.AccessLevel.NONE)
    private PermitValidity validity;

    /** The person holding this permit. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permit_holder_id", nullable = false)
    private Person permitHolder;

    /** Staff profile entitlement — set for Staff Badge (mutual exclusion). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_profile_id")
    private StaffProfile staffProfile;

    /** Contract entitlement — set for Resident Card (mutual exclusion). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    /** Work order entitlement — set for Contractor Pass (mutual exclusion). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    /** Inviting person — set for Visitor Pass (mutual exclusion). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id")
    private Person invitedBy;

    /** The staff member who created this permit in the system. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_id", nullable = false)
    private Person issuedBy;

    /**
     * Returns the permit type label for DTO compatibility.
     *
     * @return the database/API permit type label, or {@code null} when unset
     */
    public String getPermitType() {
        return permitType != null ? permitType.label() : null;
    }

    /**
     * Returns the typed permit type for domain logic.
     *
     * @return the typed permit type, or {@code null} when unset
     */
    public PermitType getPermitTypeValue() {
        return permitType;
    }

    /**
     * Returns the access level label for DTO compatibility.
     *
     * @return the database/API access level label, or {@code null} when unset
     */
    public String getAccessLevel() {
        return accessLevel != null ? accessLevel.label() : null;
    }

    /**
     * Returns the typed access level for domain logic.
     *
     * @return the typed access level, or {@code null} when unset
     */
    public AccessLevel getAccessLevelValue() {
        return accessLevel;
    }

    /**
     * Returns the permit status label for DTO compatibility.
     *
     * @return the database/API permit status label, or {@code null} when unset
     */
    public String getPermitStatus() {
        return permitStatus != null ? permitStatus.label() : null;
    }

    /**
     * Returns the typed permit status for domain logic.
     *
     * @return the typed permit status, or {@code null} when unset
     */
    public PermitStatus getPermitStatusValue() {
        return permitStatus;
    }

    /**
     * Returns the permit issue date for DTO compatibility.
     *
     * @return the permit issue date, or {@code null} when validity is unset
     */
    public LocalDate getIssueDate() {
        return validity != null ? validity.getIssueDate() : null;
    }

    /**
     * Returns the permit expiry date for DTO compatibility.
     *
     * @return the optional permit expiry date, or {@code null} when absent
     */
    public LocalDate getExpiryDate() {
        return validity != null ? validity.getExpiryDate() : null;
    }

    /**
     * Assigns the typed permit type.
     *
     * @param permitType the typed permit type
     * @throws IllegalArgumentException if the permit type is missing
     */
    public void setPermitType(PermitType permitType) {
        if (permitType == null) {
            throw new IllegalArgumentException("Permit type is required");
        }
        this.permitType = permitType;
    }

    /**
     * Assigns the optional typed access level.
     *
     * @param accessLevel the optional access level
     */
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Assigns the typed permit status.
     *
     * @param permitStatus the typed permit status
     * @throws IllegalArgumentException if the permit status is missing
     */
    public void setPermitStatus(PermitStatus permitStatus) {
        if (permitStatus == null) {
            throw new IllegalArgumentException("Permit status is required");
        }
        this.permitStatus = permitStatus;
    }

    /**
     * Assigns the permit validity period.
     *
     * @param validity the permit validity value
     * @throws IllegalArgumentException if the validity value is missing
     */
    public void setValidity(PermitValidity validity) {
        if (validity == null) {
            throw new IllegalArgumentException("Permit validity is required");
        }
        this.validity = validity;
    }
}
