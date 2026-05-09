package com.cpmss.workforce.shiftattendancetype;

import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.workforce.shiftattendancetype.dto.CreateShiftAttendanceTypeRequest;
import com.cpmss.workforce.shiftattendancetype.dto.ShiftAttendanceTypeResponse;
import com.cpmss.workforce.shiftattendancetype.dto.UpdateShiftAttendanceTypeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates shift attendance type lifecycle operations.
 *
 * @see ShiftAttendanceTypeRules
 * @see ShiftAttendanceTypeRepository
 */
@Service
public class ShiftAttendanceTypeService {

    private static final Logger log = LoggerFactory.getLogger(ShiftAttendanceTypeService.class);

    private final ShiftAttendanceTypeRepository repository;
    private final ShiftAttendanceTypeMapper mapper;
    private final ShiftAttendanceTypeRules rules = new ShiftAttendanceTypeRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository shift attendance type data access
     * @param mapper     entity-DTO mapper
     */
    public ShiftAttendanceTypeService(
            ShiftAttendanceTypeRepository repository,
            ShiftAttendanceTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a shift attendance type by its unique identifier.
     *
     * @param id the shift type's UUID primary key
     * @return the matching response
    * @throws ApiException if not found
     */
    @Transactional(readOnly = true)
    public ShiftAttendanceTypeResponse getById(UUID id) {
        ShiftAttendanceType entity = repository.findById(id)
            .orElseThrow(() -> new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND));
        return mapper.toResponse(entity);
    }

    /**
     * Lists all shift attendance types with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response
     */
    @Transactional(readOnly = true)
    public PagedResponse<ShiftAttendanceTypeResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new shift attendance type.
     *
     * @param request the create request
     * @return the created response
     */
    @Transactional
    public ShiftAttendanceTypeResponse create(CreateShiftAttendanceTypeRequest request) {
        rules.validateNameUnique(request.shiftName(), repository.existsByShiftName(request.shiftName()));
        ShiftAttendanceType entity = mapper.toEntity(request);
        entity = repository.save(entity);
        log.info("ShiftAttendanceType created: {}", entity.getShiftName());
        return mapper.toResponse(entity);
    }

    /**
     * Updates an existing shift attendance type.
     *
     * @param id      the shift type's UUID
     * @param request the update request
     * @return the updated response
    * @throws ApiException if not found
     */
    @Transactional
    public ShiftAttendanceTypeResponse update(UUID id, UpdateShiftAttendanceTypeRequest request) {
        ShiftAttendanceType entity = repository.findById(id)
            .orElseThrow(() -> new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND));
        if (!entity.getShiftName().equals(request.shiftName())) {
            rules.validateNameUnique(request.shiftName(), repository.existsByShiftName(request.shiftName()));
        }
        entity.setShiftName(request.shiftName());
        entity = repository.save(entity);
        log.info("ShiftAttendanceType updated: {}", entity.getShiftName());
        return mapper.toResponse(entity);
    }

    /**
     * Deletes a shift attendance type by ID.
     *
     * @param id the shift type's UUID
    * @throws ApiException if not found
     */
    @Transactional
    public void delete(UUID id) {
        ShiftAttendanceType entity = repository.findById(id)
            .orElseThrow(() -> new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND));
        repository.delete(entity);
        log.info("ShiftAttendanceType deleted: {}", entity.getShiftName());
    }
}
