package com.cpmss.building;

import com.cpmss.building.dto.BuildingResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Building} entities.
 *
 * <p>Flattens the nested {@code compound.id} and
 * {@code compound.compoundName} into response-level fields.
 */
@Mapper(componentModel = "spring")
public interface BuildingMapper {

    /**
     * Maps a {@link Building} entity to a response DTO.
     *
     * @param entity the building entity
     * @return the response DTO with flattened compound fields
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "compound.id", target = "compoundId")
    @Mapping(source = "compound.compoundName", target = "compoundName")
    BuildingResponse toResponse(Building entity);
}
