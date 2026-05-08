package com.cpmss.platform.common;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base class for JPA entities with a single UUID primary key.
 *
 * <p>Inherits audit fields from {@link BaseAuditEntity} and adds
 * the auto-generated UUID primary key. Defines identity semantics
 * via {@code equals()} and {@code hashCode()} based on UUID only —
 * never override these in subclasses.
 *
 * <p>Entities with composite primary keys ({@code @IdClass}) extend
 * {@link BaseAuditEntity} directly instead of this class.
 *
 * @see BaseAuditEntity
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseEntity that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
