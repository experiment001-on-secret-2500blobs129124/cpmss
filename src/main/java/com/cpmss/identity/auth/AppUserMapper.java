package com.cpmss.identity.auth;

import com.cpmss.identity.auth.dto.AppUserResponse;
import com.cpmss.identity.auth.dto.CreateAppUserRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link AppUser} entities.
 *
 * <p>Maps entities to response DTOs and request DTOs to entities.
 * The password hash is never included in any response — security by design.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper {

    /**
     * Maps an {@link AppUser} entity to a response DTO.
     *
     * @param entity the AppUser entity
     * @return the response DTO (excludes password hash)
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    AppUserResponse toResponse(AppUser entity);

    /**
     * Maps a create request DTO to an {@link AppUser} entity.
     *
     * <p>The password hash, active status, forcePasswordChange, and
     * audit fields are set by the service, not the mapper.
     *
     * @param request the create request
     * @return a new AppUser entity (id and audit fields are null)
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AppUser toEntity(CreateAppUserRequest request);
}
