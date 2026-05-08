package com.cpmss.property.facility;

import com.cpmss.property.facility.dto.FacilityResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Facility} entities.
 *
 * <p>Flattens nested building and optional managing company
 * references into response-level fields.
 */
@Mapper(componentModel = "spring")
public interface FacilityMapper {

    /**
     * Maps a {@link Facility} entity to a response DTO.
     *
     * @param entity the facility entity
     * @return the response DTO with flattened FK fields
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "building.id", target = "buildingId")
    @Mapping(source = "building.buildingNo", target = "buildingNo")
    @Mapping(source = "managedByCompany.id", target = "managedByCompanyId")
    @Mapping(source = "managedByCompany.companyName", target = "managedByCompanyName")
    FacilityResponse toResponse(Facility entity);
}
