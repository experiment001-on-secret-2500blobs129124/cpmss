package com.cpmss.hr.staffposition;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data repository for {@link StaffPosition} entities.
 *
 * <p>Provides CRUD via {@link JpaRepository}.
 */
public interface StaffPositionRepository extends JpaRepository<StaffPosition, UUID> {
}
