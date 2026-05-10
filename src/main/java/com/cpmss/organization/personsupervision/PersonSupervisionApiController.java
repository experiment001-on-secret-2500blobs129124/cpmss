package com.cpmss.organization.personsupervision;

import com.cpmss.organization.personsupervision.dto.CreatePersonSupervisionRequest;
import com.cpmss.organization.personsupervision.dto.EndPersonSupervisionRequest;
import com.cpmss.organization.personsupervision.dto.PersonSupervisionResponse;
import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for supervisor-to-staff relationship workflows.
 *
 * @see PersonSupervisionService
 */
@RestController
public class PersonSupervisionApiController {

    private final PersonSupervisionService service;

    /**
     * Constructs the controller with its service.
     *
     * @param service supervision orchestration service
     */
    public PersonSupervisionApiController(PersonSupervisionService service) {
        this.service = service;
    }

    /**
     * Creates a supervision relationship.
     *
     * @param request the relationship details
     * @return 201 Created with the supervision row
     */
    @PostMapping(ApiPaths.PERSON_SUPERVISIONS)
    public ResponseEntity<ApiResponse<PersonSupervisionResponse>> create(
            @Valid @RequestBody CreatePersonSupervisionRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(service.create(request)));
    }

    /**
     * Ends a supervision relationship.
     *
     * @param request the relationship identity and end date
     * @return 200 OK with the ended row
     */
    @PutMapping(ApiPaths.PERSON_SUPERVISIONS_END)
    public ResponseEntity<ApiResponse<PersonSupervisionResponse>> end(
            @Valid @RequestBody EndPersonSupervisionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.end(request)));
    }

    /**
     * Lists active supervisees for a supervisor.
     *
     * @param supervisorId the supervisor person UUID
     * @return 200 OK with visible active relationships
     */
    @GetMapping(ApiPaths.PERSON_SUPERVISIONS_BY_SUPERVISOR)
    public ResponseEntity<ApiResponse<List<PersonSupervisionResponse>>> getActiveSupervisees(
            @PathVariable UUID supervisorId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getActiveSupervisees(supervisorId)));
    }

    /**
     * Lists active supervisors for a supervisee.
     *
     * @param superviseeId the supervisee person UUID
     * @return 200 OK with visible active relationships
     */
    @GetMapping(ApiPaths.PERSON_SUPERVISIONS_BY_SUPERVISEE)
    public ResponseEntity<ApiResponse<List<PersonSupervisionResponse>>> getActiveSupervisors(
            @PathVariable UUID superviseeId) {
        return ResponseEntity.ok(ApiResponse.ok(service.getActiveSupervisors(superviseeId)));
    }
}
