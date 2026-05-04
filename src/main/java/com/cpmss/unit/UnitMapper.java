package com.cpmss.unit;

import com.cpmss.unit.dto.UnitResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Unit} entities.
 *
 * <p>Flattens the nested {@code building.id} and
 * {@code building.buildingNo} into response-level fields.
 */
@Mapper(componentModel = "spring")
public interface UnitMapper {

    /**
     * Maps a {@link Unit} entity to a response DTO.
     *
     * @param entity the unit entity
     * @return the response DTO with flattened building fields
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "building.id", target = "buildingId")
    @Mapping(source = "building.buildingNo", target = "buildingNo")
    UnitResponse toResponse(Unit entity);
}
