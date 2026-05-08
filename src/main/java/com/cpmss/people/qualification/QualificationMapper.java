package com.cpmss.people.qualification;
import com.cpmss.people.qualification.dto.CreateQualificationRequest;
import com.cpmss.people.qualification.dto.QualificationResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
/**
 * MapStruct mapper for {@link Qualification} entities.
 */
@Mapper(componentModel = "spring")
public interface QualificationMapper {
    /**
     * Maps a {@link Qualification} entity to a response DTO.
     *
     * @param entity the qualification entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    QualificationResponse toResponse(Qualification entity);
    /**
     * Maps a create request DTO to a new entity.
     *
     * @param dto the create request
     * @return a new qualification entity
     */
    Qualification toEntity(CreateQualificationRequest dto);
}
