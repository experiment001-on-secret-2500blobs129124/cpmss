package com.cpmss.qualification;

import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.common.PagedResponse;
import com.cpmss.qualification.dto.CreateQualificationRequest;
import com.cpmss.qualification.dto.QualificationResponse;
import com.cpmss.qualification.dto.UpdateQualificationRequest;
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
 * REST controller for qualification CRUD operations.
 *
 * @see QualificationService
 */
@RestController
public class QualificationApiController {

    private final QualificationService qualificationService;

    /**
     * Constructs the controller with the qualification service.
     *
     * @param qualificationService qualification orchestration service
     */
    public QualificationApiController(QualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    /**
     * Lists all qualifications with pagination.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated qualification list
     */
    @GetMapping(ApiPaths.QUALIFICATIONS)
    public ResponseEntity<ApiResponse<PagedResponse<QualificationResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(qualificationService.listAll(pageable)));
    }

    /**
     * Retrieves a single qualification by ID.
     *
     * @param id the qualification UUID
     * @return 200 OK with the qualification
     */
    @GetMapping(ApiPaths.QUALIFICATIONS_BY_ID)
    public ResponseEntity<ApiResponse<QualificationResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(qualificationService.getById(id)));
    }

    /**
     * Creates a new qualification.
     *
     * @param request the qualification name
     * @return 201 Created with the new qualification
     */
    @PostMapping(ApiPaths.QUALIFICATIONS)
    public ResponseEntity<ApiResponse<QualificationResponse>> create(
            @Valid @RequestBody CreateQualificationRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(qualificationService.create(request)));
    }

    /**
     * Updates an existing qualification.
     *
     * @param id      the qualification UUID
     * @param request the updated qualification name
     * @return 200 OK with the updated qualification
     */
    @PutMapping(ApiPaths.QUALIFICATIONS_BY_ID)
    public ResponseEntity<ApiResponse<QualificationResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateQualificationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(qualificationService.update(id, request)));
    }

    /**
     * Deletes a qualification by ID.
     *
     * @param id the qualification UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.QUALIFICATIONS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        qualificationService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
