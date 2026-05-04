package com.cpmss.department;

import com.cpmss.department.dto.CreateDepartmentRequest;
import com.cpmss.department.dto.DepartmentResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link Department} entities.
 *
 * <p>Maps between entities and DTOs. The {@code toResponse} method
 * uses {@code @BeanMapping(builder = @Builder(disableBuilder = true))}
 * to target the record's all-args constructor.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    /**
     * Maps a {@link Department} entity to a response DTO.
     *
     * @param entity the department entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    DepartmentResponse toResponse(Department entity);

    /**
     * Maps a create request DTO to a new entity.
     *
     * @param dto the create request
     * @return a new department entity
     */
    Department toEntity(CreateDepartmentRequest dto);
}
