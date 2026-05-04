package com.cpmss.kpipolicy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link KpiPolicy} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface KpiPolicyRepository extends JpaRepository<KpiPolicy, UUID> {
}
