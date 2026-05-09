package com.cpmss.hr.staffprofile;

import com.cpmss.people.person.Person;
import com.cpmss.people.qualification.Qualification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Detail entity (1:1 extension of {@link Person}) holding staff-specific attributes.
 *
 * <p>Only persons with the 'Staff' role have a row here. The primary key
 * is {@code person_id} — a shared PK with the {@link Person} table.
 * Created automatically when a person is assigned the Staff role.
 *
 * @see com.cpmss.people.person.PersonService
 */
@Entity
@Table(name = "Staff_Profile")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffProfile {

    /** Shared primary key — same as the person's UUID. */
    @Id
    @Column(name = "person_id", nullable = false)
    private UUID id;

    /** The person this profile extends (1:1 relationship). */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /** The staff member's highest qualification. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id", nullable = false)
    private Qualification qualification;

    /** Date the qualification was obtained. */
    @Column(name = "qualification_date")
    private LocalDate qualificationDate;

    /** URL or path to the uploaded CV file in object storage (MinIO). */
    @Column(name = "cv_file_url", length = 500)
    private String cvFileUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 255, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 255)
    private String updatedBy;
}
