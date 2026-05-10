package com.cpmss.workforce.shiftattendancetype;

import com.cpmss.hr.common.HrAccessRules;
import com.cpmss.hr.common.HrErrorCode;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendance;
import com.cpmss.hr.lawofshiftattendance.LawOfShiftAttendanceRepository;
import com.cpmss.hr.lawofshiftattendance.dto.CreateShiftAttendanceLawRequest;
import com.cpmss.hr.lawofshiftattendance.dto.ShiftAttendanceLawResponse;
import com.cpmss.identity.auth.CurrentUserService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Orchestrates shift attendance type lifecycle and attendance-law operations.
 *
 * @see ShiftAttendanceTypeRules
 * @see ShiftAttendanceTypeRepository
 */
@Service
public class ShiftAttendanceTypeService {

    private static final Logger log = LoggerFactory.getLogger(ShiftAttendanceTypeService.class);

    private final ShiftAttendanceTypeRepository repository;
    private final LawOfShiftAttendanceRepository lawRepository;
    private final ShiftAttendanceTypeMapper mapper;
    private final CurrentUserService currentUserService;
    private final ShiftAttendanceTypeRules rules = new ShiftAttendanceTypeRules();
    private final HrAccessRules accessRules = new HrAccessRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository         shift attendance type data access
     * @param lawRepository      attendance-law data access
     * @param mapper             entity-DTO mapper
     * @param currentUserService current user resolver
     */
    public ShiftAttendanceTypeService(
            ShiftAttendanceTypeRepository repository,
            LawOfShiftAttendanceRepository lawRepository,
            ShiftAttendanceTypeMapper mapper,
            CurrentUserService currentUserService) {
        this.repository = repository;
        this.lawRepository = lawRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
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
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        ShiftAttendanceType entity = findOrThrow(id);
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
        accessRules.requireHrAdministrator(currentUserService.currentUser());
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
        accessRules.requireHrAdministrator(currentUserService.currentUser());
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
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        ShiftAttendanceType entity = findOrThrow(id);
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
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        ShiftAttendanceType entity = findOrThrow(id);
        repository.delete(entity);
        log.info("ShiftAttendanceType deleted: {}", entity.getShiftName());
    }

    /**
     * Adds an effective-dated attendance law to a shift type.
     *
     * @param shiftId the shift type UUID
     * @param request the attendance law details
     * @return the created attendance law
     * @throws ApiException if the shift does not exist or the law is duplicated
     */
    @Transactional
    public ShiftAttendanceLawResponse addAttendanceLaw(
            UUID shiftId, CreateShiftAttendanceLawRequest request) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        ShiftAttendanceType shift = findOrThrow(shiftId);
        if (lawRepository.existsByShiftIdAndEffectiveDate(shiftId, request.effectiveDate())) {
            throw new ApiException(HrErrorCode.SHIFT_ATTENDANCE_LAW_DUPLICATE);
        }

        LawOfShiftAttendance law = new LawOfShiftAttendance();
        law.setShift(shift);
        law.setEffectiveDate(request.effectiveDate());
        law.setShiftTimeWindow(request.shiftTimeWindow());
        law.setExpectedHours(request.expectedHours());
        law.setOneHourExtraBonus(request.oneHourExtraBonus());
        law.setOneHourDiffDiscount(request.oneHourDiffDiscount());
        law.setPeriodStartEnd(request.periodStartEnd());
        law = lawRepository.save(law);
        log.info("Shift attendance law added: shift={}, effective={}",
                shiftId, request.effectiveDate());
        return toLawResponse(law);
    }

    /**
     * Lists attendance laws for a shift type.
     *
     * @param shiftId the shift type UUID
     * @return attendance laws, newest first
     * @throws ApiException if the shift does not exist
     */
    @Transactional(readOnly = true)
    public List<ShiftAttendanceLawResponse> getAttendanceLaws(UUID shiftId) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        if (!repository.existsById(shiftId)) {
            throw new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND);
        }
        return lawRepository.findByShiftIdOrderByEffectiveDateDesc(shiftId)
                .stream()
                .map(this::toLawResponse)
                .toList();
    }

    /**
     * Retrieves the law currently effective for a shift type.
     *
     * @param shiftId the shift type UUID
     * @return the current effective law
     * @throws ApiException if the shift or current law does not exist
     */
    @Transactional(readOnly = true)
    public ShiftAttendanceLawResponse getCurrentAttendanceLaw(UUID shiftId) {
        accessRules.requireHrAdministrator(currentUserService.currentUser());
        if (!repository.existsById(shiftId)) {
            throw new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND);
        }
        return lawRepository
                .findFirstByShiftIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                        shiftId, LocalDate.now())
                .map(this::toLawResponse)
                .orElseThrow(() -> new ApiException(HrErrorCode.SHIFT_ATTENDANCE_LAW_NOT_FOUND));
    }

    private ShiftAttendanceType findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND));
    }

    private ShiftAttendanceLawResponse toLawResponse(LawOfShiftAttendance law) {
        return new ShiftAttendanceLawResponse(
                law.getShift().getId(),
                law.getEffectiveDate(),
                law.getShiftTimeWindow(),
                law.getExpectedHours(),
                law.getOneHourExtraBonus(),
                law.getOneHourDiffDiscount(),
                law.getPeriodStartEnd());
    }
}
