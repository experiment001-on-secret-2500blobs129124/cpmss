package com.cpmss.people.person;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.people.person.dto.CreatePersonRequest;
import com.cpmss.people.person.dto.PersonResponse;
import com.cpmss.people.person.dto.UpdatePersonRequest;
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
 * REST controller for person CRUD operations.
 *
 * <p>Exposes paginated list, single-resource GET, create, update,
 * and delete endpoints under {@link ApiPaths#PERSONS}. Person
 * creation is transactional — person record, phones, emails, and
 * role assignments are all saved atomically.
 *
 * @see PersonService
 */
@RestController
public class PersonApiController {

    private final PersonService personService;

    /**
     * Constructs the controller with the person service.
     *
     * @param personService person orchestration service
     */
    public PersonApiController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Lists all persons with pagination.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return 200 OK with paginated person list
     */
    @GetMapping(ApiPaths.PERSONS)
    public ResponseEntity<ApiResponse<PagedResponse<PersonResponse>>> listAll(
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(personService.listAll(pageable)));
    }

    /**
     * Retrieves a single person by ID.
     *
     * @param id the person UUID
     * @return 200 OK with the person and their roles
     */
    @GetMapping(ApiPaths.PERSONS_BY_ID)
    public ResponseEntity<ApiResponse<PersonResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(personService.getById(id)));
    }

    /**
     * Creates a new person with roles, phones, and emails.
     *
     * @param request the person details and role IDs
     * @return 201 Created with the new person
     */
    @PostMapping(ApiPaths.PERSONS)
    public ResponseEntity<ApiResponse<PersonResponse>> create(
            @Valid @RequestBody CreatePersonRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(personService.create(request)));
    }

    /**
     * Updates an existing person.
     *
     * @param id      the person UUID
     * @param request the updated person details
     * @return 200 OK with the updated person
     */
    @PutMapping(ApiPaths.PERSONS_BY_ID)
    public ResponseEntity<ApiResponse<PersonResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePersonRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(personService.update(id, request)));
    }

    /**
     * Deletes a person by ID.
     *
     * @param id the person UUID
     * @return 204 No Content
     */
    @DeleteMapping(ApiPaths.PERSONS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        personService.delete(id);
        return ResponseEntity.status(204).body(ApiResponse.noContent());
    }
}
