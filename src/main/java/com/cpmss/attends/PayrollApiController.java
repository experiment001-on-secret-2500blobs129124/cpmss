package com.cpmss.attends;

import com.cpmss.attends.dto.AttendsResponse;
import com.cpmss.attends.dto.CreateAttendsRequest;
import com.cpmss.common.ApiPaths;
import com.cpmss.common.ApiResponse;
import com.cpmss.staffsalaryhistory.dto.CreateStaffSalaryHistoryRequest;
import com.cpmss.staffsalaryhistory.dto.StaffSalaryHistoryResponse;
import com.cpmss.taskmonthlysalary.dto.TaskMonthlySalaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for attendance, payroll, and salary operations.
 *
 * @see PayrollService
 */
@RestController
public class PayrollApiController {

    private final PayrollService payrollService;

    public PayrollApiController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /**
     * Records daily attendance for a staff member.
     *
     * @param request the attendance details
     * @return 201 Created with the attendance record
     */
    @PostMapping(ApiPaths.ATTENDANCE)
    public ResponseEntity<ApiResponse<AttendsResponse>> recordAttendance(
            @Valid @RequestBody CreateAttendsRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(payrollService.recordAttendance(request)));
    }

    /**
     * Retrieves attendance records for a staff member in a given month.
     *
     * @param staffId the staff member UUID
     * @param year    the year
     * @param month   the month (1-12)
     * @return 200 OK with attendance records
     */
    @GetMapping(ApiPaths.ATTENDANCE)
    public ResponseEntity<ApiResponse<List<AttendsResponse>>> getAttendance(
            @RequestParam UUID staffId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(
                payrollService.getAttendanceByStaff(staffId, year, month)));
    }

    /**
     * Closes monthly payroll for a department.
     *
     * @param departmentId the department UUID
     * @param year         the payroll year
     * @param month        the payroll month
     * @return 201 Created with the monthly salary records
     */
    @PostMapping(ApiPaths.PAYROLL_CLOSE)
    public ResponseEntity<ApiResponse<List<TaskMonthlySalaryResponse>>> closePayroll(
            @RequestParam UUID departmentId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        payrollService.closeMonthlyPayroll(departmentId, year, month)));
    }

    /**
     * Retrieves monthly payroll records for a department.
     *
     * @param departmentId the department UUID
     * @param year         the payroll year
     * @param month        the payroll month
     * @return 200 OK with monthly salary records
     */
    @GetMapping(ApiPaths.PAYROLL)
    public ResponseEntity<ApiResponse<List<TaskMonthlySalaryResponse>>> getPayroll(
            @RequestParam UUID departmentId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(
                payrollService.getMonthlyPayroll(departmentId, year, month)));
    }

    /**
     * Creates a new salary history record (raise/change).
     *
     * @param request the salary change details
     * @return 201 Created with the new salary record
     */
    @PostMapping(ApiPaths.STAFF_SALARY)
    public ResponseEntity<ApiResponse<StaffSalaryHistoryResponse>> createSalaryChange(
            @Valid @RequestBody CreateStaffSalaryHistoryRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(payrollService.createSalaryChange(request)));
    }
}
