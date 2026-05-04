package com.cpmss.staffprofile;

import com.cpmss.staffprofile.dto.StaffProfileResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link StaffProfile} entities.
 *
 * <p>Flattens the person and qualification FK objects into
 * their respective fields in the response DTO.
 */
@Mapper(componentModel = "spring")
public interface StaffProfileMapper {

    /**
     * Maps a {@link StaffProfile} entity to a response DTO.
     *
     * @param entity the staff profile entity
     * @return the response DTO with flattened FK references
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "id", target = "personId")
    @Mapping(source = "person.firstName", target = "personFirstName")
    @Mapping(source = "person.lastName", target = "personLastName")
    @Mapping(source = "qualification.id", target = "qualificationId")
    @Mapping(source = "qualification.qualificationName", target = "qualificationName")
    StaffProfileResponse toResponse(StaffProfile entity);
}
