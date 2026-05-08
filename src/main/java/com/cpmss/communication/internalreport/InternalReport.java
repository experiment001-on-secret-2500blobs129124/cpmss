package com.cpmss.communication.internalreport;

import com.cpmss.platform.common.BaseEntity;
import com.cpmss.people.person.Person;
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

import java.time.OffsetDateTime;

/**
 * Core entity: an internal report filed by any staff member, directed at a role group.
 *
 * <p>Reports are never deleted — closed by status change.
 * The {@code isRead} flag doubles as a notification indicator:
 * unread reports show a badge count in the frontend.
 *
 * <p>Uses a pool model: {@code assignedToRole} targets a system role
 * (e.g. "HR_OFFICER"). Any user with that role can view and act on it.
 *
 * @see com.cpmss.identity.auth.SystemRole
 */
@Entity
@Table(name = "Internal_Report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalReport extends BaseEntity {

    /** The person who filed this report. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Person reporter;

    /** The system role group that should see this report (pool model). */
    @Column(name = "assigned_to_role", nullable = false, length = 30)
    private String assignedToRole;

    /** Short subject line summarising the report. */
    @Column(nullable = false, length = 200)
    private String subject;

    /** Full report body / description. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /** Category of the report (e.g. Salary_Request, Complaint). */
    @Column(name = "report_category", nullable = false, length = 50)
    private String reportCategory;

    /** Priority level (Low, Normal, High, Urgent). Defaults to Normal. */
    @Column(nullable = false, length = 20)
    private String priority;

    /** Current status (Open, In_Review, Resolved, Rejected). Defaults to Open. */
    @Column(name = "report_status", nullable = false, length = 20)
    private String reportStatus;

    /** Whether the report has been read by someone in the target role group. */
    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    /** Timestamp when the report was first read. */
    @Column(name = "read_at")
    private OffsetDateTime readAt;

    /** The person who first read the report. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "read_by_id")
    private Person readBy;

    /** The person who resolved the report. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private Person resolvedBy;

    /** Timestamp when the report was resolved. */
    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    /** Resolution note / explanation. */
    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;
}
