
package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class pPhoto implements Comparable<pPhoto> {

    @Id
    private String uuid;

    private String fileName;

    private int pno;

    // 연관관계 설정 - Post와의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "post_id") // 외래 키 컬럼 명시적으로 지정
    private Post post;

    @Override
    public int compareTo(pPhoto other) {
        return Integer.compare(this.pno, other.pno); // 안전한 비교 메서드 사용
    }

    // 연관된 Post를 변경하는 메서드
    public void changePost(Post post) {
        this.post = post;
    }
}
