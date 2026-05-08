package com.cpmss.leasing.contract;

import com.cpmss.leasing.contract.dto.ContractResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Contract} entities.
 *
 * <p>Flattens the two optional target FK objects (unit, facility)
 * into their respective UUID fields in the response.
 */
@Mapper(componentModel = "spring")
public interface ContractMapper {

    /**
     * Maps a {@link Contract} entity to a response DTO.
     *
     * @param entity the contract entity
     * @return the response DTO with target UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "unit.id", target = "unitId")
    @Mapping(source = "facility.id", target = "facilityId")
    ContractResponse toResponse(Contract entity);
}
