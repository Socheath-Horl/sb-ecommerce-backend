package com.ecommerce.project.dtos;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Data
@NoArgsConstructor
public class PaginationResponseDto<T, E> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private Long count;
    private String previous;
    private String next;

    public PaginationResponseDto(HttpServletRequest request, List<T> content, Page<E> page) {
        this.content = content;
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.count = page.getTotalElements();

        // Build from current request (keeps all params like sort)
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(request.getRequestURL().toString())
                .query(request.getQueryString() == null ? "" : request.getQueryString());
        // Previous
        if (page.hasPrevious()) {
            String prev = builder
                    .replaceQueryParam("page", page.getNumber())
                    .replaceQueryParam("size", page.getSize())
                    .toUriString();

            this.setPrevious(prev);
        }

        // Next
        if (page.hasNext()) {
            String next = builder
                    .replaceQueryParam("page", page.getNumber() + 2)
                    .replaceQueryParam("size", page.getSize())
                    .toUriString();

            this.setNext(next);
        }
    }
}
