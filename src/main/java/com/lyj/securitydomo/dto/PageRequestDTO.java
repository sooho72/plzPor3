package com.lyj.securitydomo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1; // 현재 페이지 번호
    @Builder.Default
    private int size = 8; // 한 페이지에 보여줄 데이터 수

    private String type; // 검색의 종류
    private String keyword; // 검색어

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split(","); // 여러 검색 타입을 쉼표로 구분
    }

    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page - 1, this.size,
                Sort.by(props).descending());
    }

    private String link;

    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if (type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }
            if (keyword != null) {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // 예외 처리 추가
                }
            }
            link = builder.toString();
        }
        return link;
    }
}