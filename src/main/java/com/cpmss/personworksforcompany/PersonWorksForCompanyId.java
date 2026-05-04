package com.cpmss.personworksforcompany;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for the {@link PersonWorksForCompany} entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonWorksForCompanyId implements Serializable {

    /** The employee person. */
    private UUID employee;

    /** The employing company. */
    private UUID company;
}
