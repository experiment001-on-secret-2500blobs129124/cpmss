package com.cpmss.performance.common;

import com.cpmss.platform.exception.ApiException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Performance tier label used by KPI policies, summaries, and reviews.
 *
 * <p>Labels must match the Flyway V2 KPI and review check constraints.
 */
public enum PerformanceRating {
    /** Excellent performance tier. */
    EXCELLENT("Excellent"),
    /** Good performance tier. */
    GOOD("Good"),
    /** Average performance tier. */
    AVERAGE("Average"),
    /** Poor performance tier. */
    POOR("Poor");

    private final String label;

    PerformanceRating(String label) {
        this.label = label;
    }

    /**
     * Returns the database label.
     *
     * @return the exact performance rating label
     */
    public String label() {
        return label;
    }

    /**
     * Parses a required performance rating label.
     *
     * @param label the performance rating label
     * @return the matching rating
     * @throws ApiException if the label is missing or unsupported
     */
    public static PerformanceRating fromLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new ApiException(PerformanceErrorCode.PERFORMANCE_RATING_REQUIRED);
        }
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new ApiException(PerformanceErrorCode.PERFORMANCE_RATING_REQUIRED));
    }

    /**
     * Parses an optional performance rating label.
     *
     * @param label the optional performance rating label
     * @return the matching rating, or {@code null} when absent
     * @throws ApiException if the label is blank or unsupported
     */
    public static PerformanceRating fromNullableLabel(String label) {
        return label != null ? fromLabel(label) : null;
    }

    /**
     * Lists labels accepted by the database constraints.
     *
     * @return comma-separated performance rating labels
     */
    public static String allowedLabels() {
        return Arrays.stream(values()).map(PerformanceRating::label).collect(Collectors.joining(", "));
    }
}
