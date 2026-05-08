package com.cpmss.security.accesspermit;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.leasing.contract.Contract;
import com.cpmss.people.person.Person;
import com.cpmss.hr.staffprofile.StaffProfile;
import com.cpmss.maintenance.workorder.WorkOrder;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
@AttributeOverride(name = "id", column = @Column(name = "permit_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessPermit extends BaseEntity {

    /** System-unique permit number. */
    @Column(name = "permit_no", nullable = false, unique = true, length = 20)
    private String permitNo;

    /** Permit type (Staff Badge, Resident Card, Contractor Pass, Visitor Pass). */
    @Column(name = "permit_type", nullable = false, length = 50)
    private String permitType;

    /** Access level (Full, Restricted, Emergency Only). */
    @Column(name = "access_level", length = 50)
    private String accessLevel;

    /** Lifecycle status (Active, Suspended, Revoked, Expired). */
    @Column(name = "permit_status", nullable = false, length = 50)
    private String permitStatus;

    /** Date the permit was issued. */
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    /** Date the permit expires ({@code null} = no expiry). */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

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
}
