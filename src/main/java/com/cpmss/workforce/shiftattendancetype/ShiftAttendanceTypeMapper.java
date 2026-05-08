package com.cpmss.workforce.shiftattendancetype;
import com.cpmss.workforce.shiftattendancetype.dto.CreateShiftAttendanceTypeRequest;
import com.cpmss.workforce.shiftattendancetype.dto.ShiftAttendanceTypeResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
/**
 * MapStruct mapper for {@link ShiftAttendanceType} entities.
 */
@Mapper(componentModel = "spring")
public interface ShiftAttendanceTypeMapper {
    /**
     * Maps a {@link ShiftAttendanceType} entity to a response DTO.
     *
     * @param entity the shift attendance type entity
     * @return the response DTO
     */
    @BeanMapping(builder = @Builder(disableBuilder = true))
    ShiftAttendanceTypeResponse toResponse(ShiftAttendanceType entity);
    /**
     * Maps a create request DTO to a new entity.
     *
     * @param dto the create request
     * @return a new shift attendance type entity
     */
    ShiftAttendanceType toEntity(CreateShiftAttendanceTypeRequest dto);
}
