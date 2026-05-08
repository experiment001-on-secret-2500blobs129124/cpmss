package com.cpmss.workforce.assignedtask;

import com.cpmss.workforce.assignedtask.dto.AssignedTaskResponse;
import com.cpmss.workforce.assignedtask.dto.CreateAssignedTaskRequest;
import com.cpmss.workforce.assignedtask.dto.UpdateAssignedTaskRequest;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ResourceNotFoundException;
import com.cpmss.people.person.Person;
import com.cpmss.people.person.PersonRepository;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceType;
import com.cpmss.workforce.shiftattendancetype.ShiftAttendanceTypeRepository;
import com.cpmss.workforce.task.Task;
import com.cpmss.workforce.task.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates task assignment operations.
 *
 * @see AssignedTaskRepository
 */
@Service
public class AssignedTaskService {

    private static final Logger log = LoggerFactory.getLogger(AssignedTaskService.class);

    private final AssignedTaskRepository repository;
    private final PersonRepository personRepository;
    private final TaskRepository taskRepository;
    private final ShiftAttendanceTypeRepository shiftRepository;
    private final AssignedTaskMapper mapper;
    private final AssignedTaskRules rules = new AssignedTaskRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository       assigned task data access
     * @param personRepository person data access (staff FK lookup)
     * @param taskRepository   task data access (FK lookup)
     * @param shiftRepository  shift type data access (FK lookup)
     * @param mapper           entity-DTO mapper
     */
    public AssignedTaskService(AssignedTaskRepository repository,
                               PersonRepository personRepository,
                               TaskRepository taskRepository,
                               ShiftAttendanceTypeRepository shiftRepository,
                               AssignedTaskMapper mapper) {
        this.repository = repository;
        this.personRepository = personRepository;
        this.taskRepository = taskRepository;
        this.shiftRepository = shiftRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a task assignment by its unique identifier.
     *
     * @param id the assignment's UUID
     * @return the matching assignment response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public AssignedTaskResponse getById(UUID id) {
        return mapper.toResponse(findOrThrow(id));
    }

    /**
     * Lists all task assignments with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of assignment DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<AssignedTaskResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new task assignment.
     *
     * @param request the assignment details
     * @return the created assignment response
     */
    @Transactional
    public AssignedTaskResponse create(CreateAssignedTaskRequest request) {
        rules.validateNoDuplicateAssignment(
                request.staffId(), request.taskId(), request.assignmentDate(),
                repository.existsByStaffIdAndTaskIdAndAssignmentDate(
                        request.staffId(), request.taskId(), request.assignmentDate()));

        Person staff = personRepository.findById(request.staffId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.staffId()));
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", request.taskId()));
        ShiftAttendanceType shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ShiftAttendanceType", request.shiftId()));

        AssignedTask assignment = AssignedTask.builder()
                .staff(staff)
                .task(task)
                .shift(shift)
                .assignmentDate(request.assignmentDate())
                .dutyDescription(request.dutyDescription())
                .build();
        assignment = repository.save(assignment);
        log.info("Task assignment created: {} for staff {}", assignment.getId(), request.staffId());
        return mapper.toResponse(assignment);
    }

    /**
     * Updates an existing task assignment (duty description only).
     *
     * @param id      the assignment's UUID
     * @param request the update request
     * @return the updated assignment response
     * @throws ResourceNotFoundException if not found
     */
    @Transactional
    public AssignedTaskResponse update(UUID id, UpdateAssignedTaskRequest request) {
        AssignedTask assignment = findOrThrow(id);
        assignment.setDutyDescription(request.dutyDescription());
        assignment = repository.save(assignment);
        log.info("Task assignment updated: {}", assignment.getId());
        return mapper.toResponse(assignment);
    }

    private AssignedTask findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssignedTask", id));
    }
}
