package com.cpmss.hr.recruitment;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link Recruitment} entity (5 fields).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RecruitmentId implements Serializable {

    /** The interviewer. */
    private UUID interviewer;

    /** The applicant. */
    private UUID applicant;

    /** The position applied for. */
    private UUID position;

    /** The application date. */
    private LocalDate applicationDate;

    /** The interview date. */
    private LocalDate interviewDate;
}
