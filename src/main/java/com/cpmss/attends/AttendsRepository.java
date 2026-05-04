package com.cpmss.attends;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link Attends} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface AttendsRepository extends JpaRepository<Attends, AttendsId> {
}
