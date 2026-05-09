package com.cpmss.communication.internalreport;

import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalReportValueTest {

    @Test
    void parsesReportVocabularyLabels() {
        assertThat(ReportCategory.fromLabel("Salary_Request")).isEqualTo(ReportCategory.SALARY_REQUEST);
        assertThat(ReportCategory.fromLabel("Security_Incident")).isEqualTo(ReportCategory.SECURITY_INCIDENT);
        assertThat(ReportPriority.fromLabel("Urgent")).isEqualTo(ReportPriority.URGENT);
        assertThat(ReportStatus.fromLabel("In_Review")).isEqualTo(ReportStatus.IN_REVIEW);
    }

    @Test
    void rejectsUnknownReportVocabularyLabels() {
        assertThatThrownBy(() -> ReportCategory.fromLabel("Leave_Request"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Report category must be one of: Salary_Request, Transfer_Request, Complaint, "
                        + "Maintenance_Request, Security_Incident, Policy_Suggestion, General");
        assertThatThrownBy(() -> ReportStatus.fromLabel("Closed"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Report status must be one of: Open, In_Review, Resolved, Rejected");
    }

    @Test
    void validatesReportReceiverRoles() {
        InternalReportRules rules = new InternalReportRules();

        assertThatCode(() -> rules.validateAssignedToRole(SystemRole.GENERAL_MANAGER))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.validateAssignedToRole(SystemRole.STAFF))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid assigned role: 'STAFF'. Must be a report receiver role");
    }

    @Test
    void requiresReportReceiverRole() {
        assertThatThrownBy(() -> new InternalReportRules().validateAssignedToRole(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Assigned role is required");
    }

    @Test
    void convertersPreserveDatabaseLabels() {
        assertThat(new ReportCategoryConverter().convertToDatabaseColumn(ReportCategory.POLICY_SUGGESTION))
                .isEqualTo("Policy_Suggestion");
        assertThat(new ReportPriorityConverter().convertToDatabaseColumn(ReportPriority.NORMAL))
                .isEqualTo("Normal");
        assertThat(new ReportStatusConverter().convertToDatabaseColumn(ReportStatus.RESOLVED))
                .isEqualTo("Resolved");
    }
}
