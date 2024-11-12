package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;  // 신고 ID 자동 생성

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;  // 신고 대상 게시글

    @Enumerated(EnumType.STRING) // 문자열로 저장
    @Column(nullable = false)
    private ReportCategory category;  // 신고 분류 열거형 (SPAM, ABUSE 등)

    @Column(nullable = false, length = 255)
    private String reason;  // 신고 사유

    @Enumerated(EnumType.STRING) // 문자열로 저장
    @Column(nullable = false)
    private ReportStatus status;  // 신고 진행 상태 (PENDING, VISIBLE, HIDDEN)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;  // 생성 날짜

    public enum ReportCategory {
        SPAM,
        ABUSE,
        ADVERTISING,
        PROMOTION,
    }

    public enum ReportStatus {
        PENDING,   // 신고 처리 대기
        VISIBLE,   // 공개 상태
        HIDDEN     // 비공개 상태
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", category=" + category +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}