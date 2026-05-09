package com.cpmss.communication.internalreport;

import com.cpmss.communication.common.CommunicationErrorCode;
import com.cpmss.identity.auth.SystemRole;
import com.cpmss.platform.exception.ApiException;
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
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getErrorCode())
                                .isEqualTo(CommunicationErrorCode.REPORT_CATEGORY_INVALID));
        assertThatThrownBy(() -> ReportStatus.fromLabel("Closed"))
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getErrorCode())
                                .isEqualTo(CommunicationErrorCode.REPORT_STATUS_INVALID));
    }

    @Test
    void validatesReportReceiverRoles() {
        InternalReportRules rules = new InternalReportRules();

        assertThatCode(() -> rules.validateAssignedToRole(SystemRole.GENERAL_MANAGER))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> rules.validateAssignedToRole(SystemRole.STAFF))
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getErrorCode())
                                .isEqualTo(CommunicationErrorCode.REPORT_TARGET_ROLE_INVALID));
    }

    @Test
    void requiresReportReceiverRole() {
        assertThatThrownBy(() -> new InternalReportRules().validateAssignedToRole(null))
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getErrorCode())
                                .isEqualTo(CommunicationErrorCode.REPORT_TARGET_ROLE_REQUIRED));
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
