package com.cpmss.staffposition;

import com.cpmss.staffposition.dto.StaffPositionResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link StaffPosition} entities.
 *
 * <p>Flattens the department FK into response fields.
 */
@Mapper(componentModel = "spring")
public interface StaffPositionMapper {

    /**
     * Maps a {@link StaffPosition} entity to a response DTO.
     *
     * @param entity the staff position entity
     * @return the response DTO with department details
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    StaffPositionResponse toResponse(StaffPosition entity);
}
