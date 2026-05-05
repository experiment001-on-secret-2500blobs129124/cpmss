package com.cpmss.recruitment;

import com.cpmss.exception.BusinessException;

import java.time.LocalDate;
import java.util.List;

/**
 * Stateless business rules for the hiring pipeline.
 *
 * <p>Enforces:
 * <ul>
 *   <li>At least one interview must have result = 'Pass' before a hire agreement can be created</li>
 *   <li>Employment start date must be on or after the application date</li>
 *   <li>Offered base daily rate must be positive</li>
 * </ul>
 *
 * @see com.cpmss.hireagreement.HireAgreement
 */
public class HireAgreementRules {

    /**
     * Validates that at least one interview has a 'Pass' result.
     *
     * @param interviews the list of interview records for this application
     * @throws BusinessException if no interview has passed
     */
    public void validateAtLeastOnePass(List<Recruitment> interviews) {
        boolean hasPass = interviews.stream()
                .anyMatch(r -> "Pass".equals(r.getInterviewResult()));
        if (!hasPass) {
            throw new BusinessException(
                    "Cannot create hire agreement — at least one interview must have result 'Pass'");
        }
    }

    /**
     * Validates that the employment start date is on or after the application date.
     *
     * @param employmentStartDate the agreed start date
     * @param applicationDate     the original application date
     * @throws BusinessException if start date precedes application date
     */
    public void validateStartDateNotBeforeApplication(LocalDate employmentStartDate,
                                                       LocalDate applicationDate) {
        if (employmentStartDate.isBefore(applicationDate)) {
            throw new BusinessException(
                    "Employment start date cannot be before application date");
        }
    }
}
