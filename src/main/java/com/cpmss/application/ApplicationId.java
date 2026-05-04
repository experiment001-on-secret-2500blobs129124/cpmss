package com.cpmss.application;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Composite key for the {@link Application} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApplicationId implements Serializable {

    /** The person applying. */
    private UUID applicant;

    /** The position applied for. */
    private UUID position;

    /** The date of the application. */
    private LocalDate applicationDate;
}
