package com.cpmss.people.role;

import com.cpmss.people.role.dto.CreateRoleRequest;
import com.cpmss.people.role.dto.RoleResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link Role} entities.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Maps a {@link Role} entity to a response DTO.
     *
     * @param entity the role entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    RoleResponse toResponse(Role entity);

    /**
     * Maps a create request DTO to a new entity.
     *
     * @param dto the create request
     * @return a new role entity
     */
    Role toEntity(CreateRoleRequest dto);
}
