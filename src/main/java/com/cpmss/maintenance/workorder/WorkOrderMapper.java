package com.cpmss.maintenance.workorder;

import com.cpmss.maintenance.workorder.dto.WorkOrderResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link WorkOrder} entities.
 */
@Mapper(componentModel = "spring")
public interface WorkOrderMapper {

    /**
     * Maps a {@link WorkOrder} entity to a response DTO.
     *
     * @param entity the work order entity
     * @return the response DTO with flattened UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "facility.id", target = "facilityId")
    @Mapping(source = "company.id", target = "companyId")
    WorkOrderResponse toResponse(WorkOrder entity);
}
