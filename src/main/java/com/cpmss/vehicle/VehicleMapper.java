package com.cpmss.vehicle;

import com.cpmss.vehicle.dto.VehicleResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Vehicle} entities.
 *
 * <p>Flattens the three optional owner FK objects into their
 * respective UUID fields in the response.
 */
@Mapper(componentModel = "spring")
public interface VehicleMapper {

    /**
     * Maps a {@link Vehicle} entity to a response DTO.
     *
     * @param entity the vehicle entity
     * @return the response DTO with owner UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "ownerPerson.id", target = "ownerPersonId")
    @Mapping(source = "ownerDepartment.id", target = "ownerDepartmentId")
    @Mapping(source = "ownerCompany.id", target = "ownerCompanyId")
    VehicleResponse toResponse(Vehicle entity);
}
