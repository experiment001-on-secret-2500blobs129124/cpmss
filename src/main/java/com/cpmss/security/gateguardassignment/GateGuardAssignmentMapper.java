package com.cpmss.security.gateguardassignment;

import com.cpmss.security.gateguardassignment.dto.GateGuardAssignmentResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for {@link GateGuardAssignment} entities. */
@Mapper(componentModel = "spring")
public interface GateGuardAssignmentMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "guard.id", target = "guardId")
    @Mapping(source = "gate.id", target = "gateId")
    @Mapping(source = "taskAssignment.id", target = "taskAssignmentId")
    @Mapping(source = "shiftType.id", target = "shiftTypeId")
    GateGuardAssignmentResponse toResponse(GateGuardAssignment entity);
}
