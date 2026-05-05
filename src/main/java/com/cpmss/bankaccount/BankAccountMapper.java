package com.cpmss.bankaccount;

import com.cpmss.bankaccount.dto.BankAccountResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link BankAccount} entities.
 *
 * <p>Flattens the three optional owner FK objects into their
 * respective UUID fields in the response.
 */
@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    /**
     * Maps a {@link BankAccount} entity to a response DTO.
     *
     * @param entity the bank account entity
     * @return the response DTO with owner UUIDs
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "compound.id", target = "compoundId")
    @Mapping(source = "accountOwner.id", target = "accountOwnerId")
    @Mapping(source = "company.id", target = "companyId")
    BankAccountResponse toResponse(BankAccount entity);
}
