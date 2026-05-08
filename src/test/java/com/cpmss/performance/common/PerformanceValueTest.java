package com.cpmss.performance.common;

import com.cpmss.platform.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PerformanceValueTest {

    @Test
    void acceptsNonNegativeKpiScores() {
        KpiScore score = KpiScore.of(new BigDecimal("92.50"));

        assertThat(score.value()).isEqualByComparingTo("92.50");
    }

    @Test
    void rejectsNegativeKpiScores() {
        assertThatThrownBy(() -> KpiScore.of(new BigDecimal("-0.01")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("KPI score cannot be negative");
    }

    @Test
    void validatesKpiScoreRanges() {
        KpiScoreRange range = KpiScoreRange.of(new BigDecimal("80.00"), new BigDecimal("100.00"));

        assertThat(range.contains(KpiScore.of(new BigDecimal("80.00")))).isTrue();
        assertThat(range.contains(KpiScore.of(new BigDecimal("100.00")))).isTrue();
        assertThat(range.contains(KpiScore.of(new BigDecimal("79.99")))).isFalse();
    }

    @Test
    void rejectsInvalidKpiScoreRanges() {
        assertThatThrownBy(() -> KpiScoreRange.of(new BigDecimal("90.00"), new BigDecimal("90.00")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("KPI score range max must be greater than min");
    }

    @Test
    void defaultsMissingPercentageRateToZero() {
        assertThat(PercentageRate.orZero(null).value()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void rejectsNegativePercentageRates() {
        assertThatThrownBy(() -> PercentageRate.of(new BigDecimal("-0.0001")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Percentage rate cannot be negative");
    }

    @Test
    void parsesPerformanceRatingLabels() {
        assertThat(PerformanceRating.fromLabel("Excellent")).isEqualTo(PerformanceRating.EXCELLENT);
        assertThat(PerformanceRating.fromLabel("Good")).isEqualTo(PerformanceRating.GOOD);
        assertThat(PerformanceRating.fromLabel("Average")).isEqualTo(PerformanceRating.AVERAGE);
        assertThat(PerformanceRating.fromLabel("Poor")).isEqualTo(PerformanceRating.POOR);
    }

    @Test
    void rejectsUnknownPerformanceRatingLabels() {
        assertThatThrownBy(() -> PerformanceRating.fromLabel("Satisfactory"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Performance rating must be one of: Excellent, Good, Average, Poor");
    }

    @Test
    void convertersPreserveDatabaseValues() {
        assertThat(new KpiScoreConverter().convertToDatabaseColumn(KpiScore.of(new BigDecimal("10.25"))))
                .isEqualByComparingTo("10.25");
        assertThat(new PercentageRateConverter()
                .convertToDatabaseColumn(PercentageRate.of(new BigDecimal("0.1500"))))
                .isEqualByComparingTo("0.1500");
        assertThat(new PerformanceRatingConverter()
                .convertToDatabaseColumn(PerformanceRating.AVERAGE))
                .isEqualTo("Average");
    }
}
