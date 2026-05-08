package com.cpmss.workforce.task;

import com.cpmss.workforce.task.dto.TaskResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Task} entities.
 *
 * <p>Maps the nested {@code department.id} and {@code department.departmentName}
 * into flat response fields.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    /**
     * Maps a {@link Task} entity to a response DTO.
     *
     * @param entity the task entity
     * @return the response DTO with flattened department fields
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    TaskResponse toResponse(Task entity);
}
