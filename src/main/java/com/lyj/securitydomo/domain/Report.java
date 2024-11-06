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
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING) // 문자열로 저장
    @Column(nullable = false)
    private ReportCategory category; // 열거형으로 변경

    @Column(nullable = false, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING) // 문자열로 저장
    @Column(nullable = false)
    private ReportStatus status; // 열거형으로 변경

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public enum ReportCategory {
        SPAM,
        ABUSE,
        ADVERTISING,
        PROMOTION,
    }

    public enum ReportStatus {
        PENDING,
        COMPLETED // 필요시 추가
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