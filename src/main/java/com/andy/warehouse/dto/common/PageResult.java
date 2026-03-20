package com.andy.warehouse.dto.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResult<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;
    private boolean first;
    private boolean last;
    private boolean empty;

    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setContent(page.getContent());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setNumber(page.getNumber());
        result.setSize(page.getSize());
        result.setFirst(page.isFirst());
        result.setLast(page.isLast());
        result.setEmpty(page.isEmpty());
        return result;
    }
}
