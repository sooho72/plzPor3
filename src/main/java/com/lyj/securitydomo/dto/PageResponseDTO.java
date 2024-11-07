package com.lyj.securitydomo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;
    private int start;
    private int end;
    private boolean prev;
    private boolean next;
    private List<E> dtoList;

    @Builder
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList != null ? dtoList : List.of(); // Null 처리

        this.end = (int)(Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;
        int last = (int)(Math.ceil((double) total / size));
        this.end = Math.min(this.end, last); // 마지막 페이지 번호 조정

        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }
}