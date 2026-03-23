package com.andy.warehouse.dto.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setContent(page.getRecords());
        result.setTotalElements(page.getTotal());
        result.setTotalPages((int) page.getPages());
        result.setNumber((int) page.getCurrent() - 1);
        result.setSize((int) page.getSize());
        result.setFirst(page.getCurrent() <= 1);
        result.setLast(page.getCurrent() >= page.getPages());
        result.setEmpty(page.getRecords() == null || page.getRecords().isEmpty());
        return result;
    }

    public static <T> PageResult<T> of(List<T> content, long totalElements, long current, long size) {
        PageResult<T> result = new PageResult<>();
        result.setContent(content);
        result.setTotalElements(totalElements);
        int totalPages = (int) ((totalElements + size - 1) / size);
        result.setTotalPages(totalPages);
        result.setNumber((int) current - 1);
        result.setSize((int) size);
        result.setFirst(current <= 1);
        result.setLast(current >= totalPages);
        result.setEmpty(content == null || content.isEmpty());
        return result;
    }
}
