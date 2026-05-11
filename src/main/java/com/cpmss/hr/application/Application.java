package com.cpmss.hr.application;

import com.cpmss.platform.common.BaseAuditEntity;
import com.cpmss.people.person.Person;
import com.cpmss.hr.staffposition.StaffPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Core entity representing a job application — the root of the hiring pipeline.
 *
 * <p>Composite PK: ({@code applicant_id}, {@code position_id},
 * {@code application_date}). Application status is DERIVED — not stored.
 * If Recruitment rows exist → "Interviewing"; if Hire_Agreement → "Hired".
 */
@Entity
@Table(name = "Applications")
@IdClass(ApplicationId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseAuditEntity {

    /** The person applying (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Person applicant;

    /** The position applied for (part of composite PK). */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private StaffPosition position;

    /** The date of the application (part of composite PK). */
    @Id
    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    /** MinIO object key for the current CV attached to this application. */
    @Column(name = "cv_object_key", length = 500)
    private String cvObjectKey;

    /** Original filename displayed to authorized users. */
    @Column(name = "cv_original_filename", length = 255)
    private String cvOriginalFilename;

    /** Validated MIME type of the current CV object. */
    @Column(name = "cv_content_type", length = 100)
    private String cvContentType;

    /** Uploaded CV object size in bytes. */
    @Column(name = "cv_size_bytes")
    private Long cvSizeBytes;

    /** Timestamp when the current CV metadata was saved. */
    @Column(name = "cv_uploaded_at")
    private Instant cvUploadedAt;

    /** Person who uploaded or replaced the current CV. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_uploaded_by_id")
    private Person cvUploadedBy;
}
