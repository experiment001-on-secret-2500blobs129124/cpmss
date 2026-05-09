package com.cpmss.communication.internalreport;

import com.cpmss.communication.internalreport.dto.InternalReportResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link InternalReport} entities.
 */
@Mapper(componentModel = "spring")
public interface InternalReportMapper {

    /**
     * Converts an internal report entity to its API response DTO.
     *
     * @param entity the internal report entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "reporter.id", target = "reporterId")
    @Mapping(source = "readBy.id", target = "readById")
    @Mapping(source = "resolvedBy.id", target = "resolvedById")
    @Mapping(source = "read", target = "isRead")
    InternalReportResponse toResponse(InternalReport entity);
}
