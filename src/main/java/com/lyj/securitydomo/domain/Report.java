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
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동증가
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "reportStatusId")
    private ReportStatus reportStatus;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private ReportCategory categoryId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
