package com.cpmss.workforce.assignedtask;

import com.cpmss.workforce.assignedtask.dto.AssignedTaskResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link AssignedTask} entities.
 */
@Mapper(componentModel = "spring")
public interface AssignedTaskMapper {

    /**
     * Maps an {@link AssignedTask} entity to a response DTO.
     *
     * @param entity the assigned task entity
     * @return the response DTO with flattened UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "staff.id", target = "staffId")
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "shift.id", target = "shiftId")
    AssignedTaskResponse toResponse(AssignedTask entity);
}
