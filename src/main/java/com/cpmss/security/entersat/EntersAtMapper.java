package com.cpmss.security.entersat;

import com.cpmss.security.entersat.dto.EntersAtResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link EntersAt} entities.
 *
 * <p>Flattens FK objects into their UUID fields in the response.
 */
@Mapper(componentModel = "spring")
public interface EntersAtMapper {

    /**
     * Maps an {@link EntersAt} entity to a response DTO.
     *
     * @param entity the gate entry entity
     * @return the response DTO with flattened UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "gate.id", target = "gateId")
    @Mapping(source = "permit.id", target = "permitId")
    @Mapping(source = "processedBy.id", target = "processedById")
    @Mapping(source = "requestedBy.id", target = "requestedById")
    EntersAtResponse toResponse(EntersAt entity);
}
