package com.cpmss.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Stable pagination envelope for all list endpoints.
 *
 * <p>Wraps Spring Data's {@link Page} to decouple the API response shape
 * from Spring internals. Clients receive a consistent structure regardless
 * of Spring Data version changes.
 *
 * @param content       the items on this page
 * @param totalElements total number of items across all pages
 * @param totalPages    total number of pages
 * @param pageNumber    current page index (zero-based)
 * @param pageSize      number of items requested per page
 * @param <T>           item type
 */
public record PagedResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {

    /**
     * Builds a {@code PagedResponse} from a Spring Data {@link Page},
     * applying a mapping function to each element.
     *
     * @param page   the raw Spring Data page
     * @param mapper transform function applied to each element
     * @param <S>    source element type
     * @param <T>    target element type
     * @return a {@code PagedResponse} with mapped content
     */
    public static <S, T> PagedResponse<T> from(Page<S> page, Function<S, T> mapper) {
        return new PagedResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}
