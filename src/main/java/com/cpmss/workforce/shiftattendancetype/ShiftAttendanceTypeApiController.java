package com.cpmss.workforce.shiftattendancetype;

import com.cpmss.hr.lawofshiftattendance.dto.CreateShiftAttendanceLawRequest;
import com.cpmss.hr.lawofshiftattendance.dto.ShiftAttendanceLawResponse;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.workforce.shiftattendancetype.dto.CreateShiftAttendanceTypeRequest;
import com.cpmss.workforce.shiftattendancetype.dto.ShiftAttendanceTypeResponse;
import com.cpmss.workforce.shiftattendancetype.dto.UpdateShiftAttendanceTypeRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for shift attendance type CRUD operations.
 *
 * @see ShiftAttendanceTypeService
 */
@RestController
public class ShiftAttendanceTypeApiController {

    private final ShiftAttendanceTypeService shiftAttendanceTypeService;

    /**
     * Constructs the controller with the shift attendance type service.
     *
     * @param shiftAttendanceTypeService orchestration service
     */
    public ShiftAttendanceTypeApiController(ShiftAttendanceTypeService shiftAttendanceTypeService) {
        this.shiftAttendanceTypeService = shiftAttendanceTypeService;
    }

    /**
     * Lists all shift attendance types with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated list
     */
    @GetMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES)
    public ResponseEntity<ApiResponse<PagedResponse<ShiftAttendanceTypeResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(shiftAttendanceTypeService.listAll(pageable)));
    }

    /**
     * Retrieves a single shift attendance type by ID.
     *
     * @param id the shift type UUID
     * @return 200 OK with the shift type
     */
    @GetMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES_BY_ID)
    public ResponseEntity<ApiResponse<ShiftAttendanceTypeResponse>> getById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(shiftAttendanceTypeService.getById(id)));
    }

    /**
     * Creates a new shift attendance type.
     *
     * @param request the shift name
     * @return 201 Created
     */
    @PostMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES)
    public ResponseEntity<ApiResponse<ShiftAttendanceTypeResponse>> create(
            @Valid @RequestBody CreateShiftAttendanceTypeRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(shiftAttendanceTypeService.create(request)));
    }

    /**
     * Adds an attendance law to a shift type.
     *
     * @param id      the shift type UUID
     * @param request the law details
     * @return 201 Created with the new attendance law
     */
    @PostMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES_LAWS)
    public ResponseEntity<ApiResponse<ShiftAttendanceLawResponse>> addAttendanceLaw(
            @PathVariable UUID id,
            @Valid @RequestBody CreateShiftAttendanceLawRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        shiftAttendanceTypeService.addAttendanceLaw(id, request)));
    }

    /**
     * Retrieves attendance laws for a shift type.
     *
     * @param id the shift type UUID
     * @return 200 OK with attendance laws
     */
    @GetMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES_LAWS)
    public ResponseEntity<ApiResponse<List<ShiftAttendanceLawResponse>>> getAttendanceLaws(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                shiftAttendanceTypeService.getAttendanceLaws(id)));
    }

    /**
     * Retrieves the current attendance law for a shift type.
     *
     * @param id the shift type UUID
     * @return 200 OK with the current attendance law
     */
    @GetMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES_CURRENT_LAW)
    public ResponseEntity<ApiResponse<ShiftAttendanceLawResponse>> getCurrentAttendanceLaw(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(
                shiftAttendanceTypeService.getCurrentAttendanceLaw(id)));
    }

    /**
     * Updates an existing shift attendance type.
     *
     * @param id      the shift type UUID
     * @param request the updated shift name
     * @return 200 OK
     */
    @PutMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES_BY_ID)
    public ResponseEntity<ApiResponse<ShiftAttendanceTypeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShiftAttendanceTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(shiftAttendanceTypeService.update(id, request)));
    }

    /**
     * Deletes a shift attendance type by ID.
     *
     * @param id the shift type UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.SHIFT_ATTENDANCE_TYPES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        shiftAttendanceTypeService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
