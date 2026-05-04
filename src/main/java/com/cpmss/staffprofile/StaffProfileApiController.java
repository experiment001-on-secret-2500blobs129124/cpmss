package com.cpmss.staffprofile;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.staffprofile.dto.CreateStaffProfileRequest;
import com.cpmss.staffprofile.dto.StaffProfileResponse;
import com.cpmss.staffprofile.dto.UpdateStaffProfileRequest;
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

import java.util.UUID;

/**
 * REST controller for staff profile CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#STAFF_PROFILES}.
 *
 * @see StaffProfileService
 */
@RestController
public class StaffProfileApiController {

    private final StaffProfileService service;

    /**
     * Constructs the controller with the staff profile service.
     *
     * @param service staff profile orchestration service
     */
    public StaffProfileApiController(StaffProfileService service) {
        this.service = service;
    }

    /**
     * Lists all staff profiles with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated list
     */
    @GetMapping(ApiPaths.STAFF_PROFILES)
    public ResponseEntity<ApiResponse<PagedResponse<StaffProfileResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(service.listAll(pageable)));
    }

    /**
     * Retrieves a single staff profile by person ID.
     *
     * @param id the person's UUID (profile PK)
     * @return 200 OK with the staff profile
     */
    @GetMapping(ApiPaths.STAFF_PROFILES_BY_ID)
    public ResponseEntity<ApiResponse<StaffProfileResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    /**
     * Creates a new staff profile.
     *
     * @param request the profile details including person ID
     * @return 201 Created with the new profile
     */
    @PostMapping(ApiPaths.STAFF_PROFILES)
    public ResponseEntity<ApiResponse<StaffProfileResponse>> create(
            @Valid @RequestBody CreateStaffProfileRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(service.create(request)));
    }

    /**
     * Updates an existing staff profile.
     *
     * @param id      the person's UUID (profile PK)
     * @param request the updated profile details
     * @return 200 OK with the updated profile
     */
    @PutMapping(ApiPaths.STAFF_PROFILES_BY_ID)
    public ResponseEntity<ApiResponse<StaffProfileResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    /**
     * Deletes a staff profile by person ID.
     *
     * @param id the person's UUID (profile PK)
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.STAFF_PROFILES_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
