package com.cpmss.auth;

import com.cpmss.auth.dto.AppUserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link AppUser} entities.
 *
 * <p>Maps entities to response DTOs. The password hash is never
 * included in any response — security by design.
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
}
