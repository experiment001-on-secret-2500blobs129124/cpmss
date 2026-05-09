package com.cpmss.security.gateguardassignment;

import com.cpmss.people.common.PeopleErrorCode;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.security.common.SecurityErrorCode;
import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.security.gate.Gate;
import com.cpmss.security.gate.GateRepository;
import com.cpmss.security.gateguardassignment.dto.CreateGateGuardAssignmentRequest;
import com.cpmss.security.gateguardassignment.dto.GateGuardAssignmentResponse;
import com.cpmss.security.gateguardassignment.dto.UpdateGateGuardAssignmentRequest;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.workforce.assignedtask.AssignedTask;
import com.cpmss.workforce.assignedtask.AssignedTaskRepository;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Orchestrates gate guard assignment operations. */
@Service
public class GateGuardAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(GateGuardAssignmentService.class);

    private final GateGuardAssignmentRepository repository;
    private final PersonRepository personRepository;
    private final GateRepository gateRepository;
    private final AssignedTaskRepository assignedTaskRepository;
    private final ShiftAttendanceTypeRepository shiftRepository;
    private final GateGuardAssignmentMapper mapper;

    /**
     * Creates the service with repositories needed to resolve gate, guard,
     * task, and optional shift references.
     *
     * @param repository the gate guard assignment repository
     * @param personRepository repository used to load the assigned guard
     * @param gateRepository repository used to load the assigned gate
     * @param assignedTaskRepository repository used to load the backing task
     * @param shiftRepository repository used to load the optional shift type
     * @param mapper mapper converting entities to API responses
     */
    public GateGuardAssignmentService(GateGuardAssignmentRepository repository,
                                      PersonRepository personRepository,
                                      GateRepository gateRepository,
                                      AssignedTaskRepository assignedTaskRepository,
                                      ShiftAttendanceTypeRepository shiftRepository,
                                      GateGuardAssignmentMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.gateRepository = gateRepository;
        this.assignedTaskRepository = assignedTaskRepository;
        this.shiftRepository = shiftRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves one gate guard assignment by its identifier.
     *
     * @param id the assignment identifier
     * @return the matching gate guard assignment response
     * @throws ApiException if the assignment does not exist
     */
    @Transactional(readOnly = true)
    public GateGuardAssignmentResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists gate guard assignments using pageable repository access.
     *
     * @param pageable the paging configuration for the result set
     * @return a page of gate guard assignment responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<GateGuardAssignmentResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a gate guard assignment after resolving the referenced guard,
     * gate, task assignment, and optional shift type.
     *
     * @param request the requested gate guard assignment values
     * @return the created gate guard assignment response
     * @throws ApiException if any referenced record does not exist
     */
    @Transactional
    public GateGuardAssignmentResponse create(CreateGateGuardAssignmentRequest request) {
        Person guard = personRepository.findById(request.guardId())
            .orElseThrow(() -> new ApiException(PeopleErrorCode.PERSON_NOT_FOUND));
        Gate gate = gateRepository.findById(request.gateId())
            .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_NOT_FOUND));
        AssignedTask task = assignedTaskRepository.findById(request.taskAssignmentId())
            .orElseThrow(() -> new ApiException(WorkforceErrorCode.ASSIGNED_TASK_NOT_FOUND));

        GateGuardAssignment assignment = GateGuardAssignment.builder()
                .guard(guard)
                .gate(gate)
                .taskAssignment(task)
                .shiftType(resolveShift(request.shiftTypeId()))
                .shiftStart(request.shiftStart())
                .shiftEnd(request.shiftEnd())
                .build();
        assignment = repository.save(assignment);
        log.info("Gate guard assignment created: guard {} at gate {}", request.guardId(), request.gateId());
        return mapper.toResponse(assignment);
    }

    /**
     * Updates the recorded end of an existing gate guard assignment.
     *
     * @param id the assignment identifier
     * @param request the new assignment values
     * @return the updated gate guard assignment response
     * @throws ApiException if the assignment does not exist
     */
    @Transactional
    public GateGuardAssignmentResponse update(UUID id, UpdateGateGuardAssignmentRequest request) {
        GateGuardAssignment assignment = findOrThrow(id);
        assignment.setShiftEnd(request.shiftEnd());
        assignment = repository.save(assignment);
        log.info("Gate guard assignment updated (shift ended): {}", assignment.getId());
        return mapper.toResponse(assignment);
    }

    private GateGuardAssignment findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(SecurityErrorCode.GATE_GUARD_ASSIGNMENT_NOT_FOUND));
    }

    private ShiftAttendanceType resolveShift(UUID id) {
        if (id == null) {
            return null;
        }
        return shiftRepository.findById(id)
                .orElseThrow(() -> new ApiException(WorkforceErrorCode.SHIFT_TYPE_NOT_FOUND));
    }
}
