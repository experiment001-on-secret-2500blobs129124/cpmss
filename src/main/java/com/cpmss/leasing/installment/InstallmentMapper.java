package com.cpmss.leasing.installment;

import com.cpmss.leasing.installment.dto.InstallmentResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link Installment} entities.
 *
 * <p>Flattens the contract FK to its UUID in the response.
 */
@Mapper(componentModel = "spring")
public interface InstallmentMapper {

    /**
     * Maps an {@link Installment} entity to a response DTO.
     *
     * @param entity the installment entity
     * @return the response DTO with contract UUID
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "contract.id", target = "contractId")
    InstallmentResponse toResponse(Installment entity);
}
