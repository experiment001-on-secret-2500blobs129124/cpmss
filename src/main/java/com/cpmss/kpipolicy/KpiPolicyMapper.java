package com.cpmss.kpipolicy;

import com.cpmss.kpipolicy.dto.KpiPolicyResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for {@link KpiPolicy} entities. */
@Mapper(componentModel = "spring")
public interface KpiPolicyMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "approvedBy.id", target = "approvedById")
    KpiPolicyResponse toResponse(KpiPolicy entity);
}
