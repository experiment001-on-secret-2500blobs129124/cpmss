package com.cpmss.compound;

import com.cpmss.compound.dto.CompoundResponse;
import com.cpmss.compound.dto.CreateCompoundRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link Compound} entities.
 *
 * <p>Converts between the JPA entity and its request/response DTOs.
 * Uses {@code @BeanMapping(builder = @Builder(disableBuilder = true))}
 * on the response method because {@link CompoundResponse} is a Java
 * Record (immutable, all-args constructor).
 */
@Mapper(componentModel = "spring")
public interface CompoundMapper {

    /**
     * Maps a {@link Compound} entity to a response DTO.
     *
     * @param entity the compound entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    CompoundResponse toResponse(Compound entity);

    /**
     * Maps a create request DTO to a new entity.
     *
     * @param dto the create request
     * @return a new compound entity (unsaved)
     */
    Compound toEntity(CreateCompoundRequest dto);
}
