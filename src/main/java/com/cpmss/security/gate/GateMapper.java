package com.cpmss.security.gate;

import com.cpmss.security.gate.dto.GateResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Gate} entities.
 *
 * <p>Flattens the nested {@code compound.id} and
 * {@code compound.compoundName} into response-level fields.
 */
@Mapper(componentModel = "spring")
public interface GateMapper {

    /**
     * Maps a {@link Gate} entity to a response DTO.
     *
     * @param entity the gate entity
     * @return the response DTO with flattened compound fields
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "compound.id", target = "compoundId")
    @Mapping(source = "compound.compoundName", target = "compoundName")
    GateResponse toResponse(Gate entity);
}
