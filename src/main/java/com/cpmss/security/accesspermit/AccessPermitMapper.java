package com.cpmss.security.accesspermit;

import com.cpmss.security.accesspermit.dto.AccessPermitResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link AccessPermit} entities.
 *
 * <p>Flattens the five FK objects into their UUID fields in the response.
 */
@Mapper(componentModel = "spring")
public interface AccessPermitMapper {

    /**
     * Maps an {@link AccessPermit} entity to a response DTO.
     *
     * @param entity the access permit entity
     * @return the response DTO with flattened UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "permitHolder.id", target = "permitHolderId")
    @Mapping(source = "staffProfile.id", target = "staffProfileId")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "workOrder.id", target = "workOrderId")
    @Mapping(source = "invitedBy.id", target = "invitedById")
    @Mapping(source = "issuedBy.id", target = "issuedById")
    AccessPermitResponse toResponse(AccessPermit entity);
}
