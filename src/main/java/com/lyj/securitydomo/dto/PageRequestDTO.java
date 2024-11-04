
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
    private int page = 1;
    @Builder.Default
    private int size = 10;

    private String type; // 검색의종류

    private String keyword;

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return new String[0]; // 빈 배열 반환
        }
        return type.split("");
    }

    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page - 1, this.size,
                Sort.by(props).descending());
    }

    private String link;

    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=").append(this.page)
                    .append("&size=").append(this.size);
            if (type != null && !type.isEmpty()) {
                builder.append("&type=").append(type);
            }
            if (keyword != null && !keyword.isEmpty()) {
                try {
                    builder.append("&keyword=").append(URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // 예외 처리 로직 추가
                }
            }
            link = builder.toString();
        }
        return link;
    }
}
