package com.cpmss.workforce.task;

import com.cpmss.organization.common.OrganizationErrorCode;
import com.cpmss.organization.department.Department;
import com.cpmss.organization.department.DepartmentRepository;
import com.cpmss.platform.common.PagedResponse;
import com.cpmss.platform.exception.ApiException;
import com.cpmss.workforce.common.WorkforceErrorCode;
import com.cpmss.workforce.task.dto.CreateTaskRequest;
import com.cpmss.workforce.task.dto.TaskResponse;
import com.cpmss.workforce.task.dto.UpdateTaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates task lifecycle operations.
 *
 * <p>Task is scoped to a {@link Department} — uniqueness of
 * {@code taskTitle} is enforced per department, not globally.
 *
 * @see TaskRules
 * @see TaskRepository
 */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final DepartmentRepository departmentRepository;
    private final TaskMapper mapper;
    private final TaskRules rules = new TaskRules();

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository           task data access
     * @param departmentRepository department data access (for FK lookup)
     * @param mapper               entity-DTO mapper
     */
    public TaskService(
            TaskRepository repository,
            DepartmentRepository departmentRepository,
            TaskMapper mapper) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the task's UUID primary key
     * @return the matching task response
        * @throws ApiException if not found
     */
    @Transactional(readOnly = true)
    public TaskResponse getById(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ApiException(WorkforceErrorCode.TASK_NOT_FOUND));
        return mapper.toResponse(task);
    }

    /**
     * Lists all tasks with pagination.
     *
     * @param pageable pagination parameters
     * @return a paged response of task DTOs
     */
    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> listAll(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable), mapper::toResponse);
    }

    /**
     * Creates a new task under the specified department.
     *
     * @param request the create request with title and department ID
     * @return the created task response
        * @throws ApiException if the department does not exist
     */
    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        rules.validateTitleUniqueInDepartment(
                request.taskTitle(),
                repository.existsByTaskTitleAndDepartmentId(
                        request.taskTitle(), request.departmentId()));

        Task task = Task.builder()
                .taskTitle(request.taskTitle())
                .department(department)
                .build();
        task = repository.save(task);
        log.info("Task created: {} in {}", task.getTaskTitle(), department.getDepartmentName());
        return mapper.toResponse(task);
    }

    /**
     * Updates an existing task.
     *
     * @param id      the task's UUID
     * @param request the update request
     * @return the updated task response
        * @throws ApiException if not found
     */
    @Transactional
    public TaskResponse update(UUID id, UpdateTaskRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ApiException(WorkforceErrorCode.TASK_NOT_FOUND));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ApiException(OrganizationErrorCode.DEPARTMENT_NOT_FOUND));

        boolean titleChanged = !task.getTaskTitle().equals(request.taskTitle());
        boolean deptChanged = !task.getDepartment().getId().equals(request.departmentId());

        if (titleChanged || deptChanged) {
            rules.validateTitleUniqueInDepartment(
                    request.taskTitle(),
                    repository.existsByTaskTitleAndDepartmentId(
                            request.taskTitle(), request.departmentId()));
        }

        task.setTaskTitle(request.taskTitle());
        task.setDepartment(department);
        task = repository.save(task);
        log.info("Task updated: {}", task.getTaskTitle());
        return mapper.toResponse(task);
    }

    /**
     * Deletes a task by ID.
     *
     * @param id the task's UUID
        * @throws ApiException if not found
     */
    @Transactional
    public void delete(UUID id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ApiException(WorkforceErrorCode.TASK_NOT_FOUND));
        repository.delete(task);
        log.info("Task deleted: {}", task.getTaskTitle());
    }
}
