package com.cpmss.people.qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
/**
 * Spring Data repository for {@link Qualification} entities.
 */
public interface QualificationRepository extends JpaRepository<Qualification, UUID> {
    /**
     * Checks whether a qualification with the given name exists.
     *
     * @param qualificationName the name to check
     * @return true if a matching qualification exists
     */
    boolean existsByQualificationName(String qualificationName);
}
