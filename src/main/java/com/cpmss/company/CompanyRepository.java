package com.cpmss.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link Company} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}. Companies are identified
 * solely by UUID — no slug or unique-name lookup is needed.
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
