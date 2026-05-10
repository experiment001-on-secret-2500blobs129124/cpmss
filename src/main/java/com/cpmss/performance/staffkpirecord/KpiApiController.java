package com.cpmss.performance.staffkpirecord;

import com.cpmss.platform.common.ApiPaths;
import com.cpmss.platform.common.ApiResponse;
import com.cpmss.performance.staffkpimonthlysummary.dto.StaffKpiMonthlySummaryResponse;
import com.cpmss.performance.staffkpirecord.dto.CreateStaffKpiRecordRequest;
import com.cpmss.performance.staffkpirecord.dto.StaffKpiRecordResponse;
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
 * REST controller for KPI scoring and monthly close operations.
 *
 * @see KpiService
 */
@RestController
public class KpiApiController {

    private final KpiService kpiService;

    public KpiApiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    /**
     * Records a daily KPI score for a staff member.
     *
     * @param request the KPI record details
     * @return 201 Created with the new KPI record
     */
    @PostMapping(ApiPaths.KPI_RECORDS)
    public ResponseEntity<ApiResponse<StaffKpiRecordResponse>> recordKpi(
            @Valid @RequestBody CreateStaffKpiRecordRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(kpiService.recordDailyKpi(request)));
    }

    /**
     * Retrieves KPI records for a staff member in a given month.
     *
     * @param staffId the staff member UUID
     * @param year    the year
     * @param month   the month (1-12)
     * @return 200 OK with KPI records
     */
    @GetMapping(ApiPaths.KPI_RECORDS)
    public ResponseEntity<ApiResponse<List<StaffKpiRecordResponse>>> getKpiRecords(
            @RequestParam UUID staffId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(kpiService.getKpiByStaff(staffId, year, month)));
    }

    /**
     * Closes monthly KPI for a department.
     *
     * @param departmentId the department UUID
     * @param year         the year
     * @param month        the month
     * @param closedById   the manager/HR who is closing
     * @return 201 Created with the summary records
     */
    @PostMapping(ApiPaths.KPI_CLOSE)
    public ResponseEntity<ApiResponse<List<StaffKpiMonthlySummaryResponse>>> closeKpi(
            @RequestParam UUID departmentId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam UUID closedById) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        kpiService.closeMonthlyKpi(departmentId, year, month, closedById)));
    }

    /**
     * Retrieves KPI summaries for a department or staff member in a period.
     *
     * @param departmentId optional department UUID for broad department summaries
     * @param staffId      optional staff UUID for self-scoped summaries
     * @param year         the year
     * @param month        the month
     * @return 200 OK with summary records
     */
    @GetMapping(ApiPaths.KPI_SUMMARIES)
    public ResponseEntity<ApiResponse<List<StaffKpiMonthlySummaryResponse>>> getKpiSummaries(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID staffId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(ApiResponse.ok(
                kpiService.getKpiSummaries(departmentId, staffId, year, month)));
    }
}
