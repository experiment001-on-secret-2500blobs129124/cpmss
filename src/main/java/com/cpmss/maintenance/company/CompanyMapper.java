package com.cpmss.maintenance.company;

import com.cpmss.maintenance.company.dto.CompanyResponse;
import com.cpmss.maintenance.company.dto.CreateCompanyRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link Company} entities.
 *
 * <p>Converts between the JPA entity and its request/response DTOs.
 * Uses {@code @BeanMapping(builder = @Builder(disableBuilder = true))}
 * on the response method because {@link CompanyResponse} is a Java
 * Record (immutable, all-args constructor).
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {

    /**
     * Maps a {@link Company} entity to a response DTO.
     *
     * @param entity the company entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    CompanyResponse toResponse(Company entity);

    /**
     * Maps a create request DTO to a new entity.
     *
     * @param dto the create request
     * @return a new company entity (unsaved)
     */
    Company toEntity(CreateCompanyRequest dto);
}
