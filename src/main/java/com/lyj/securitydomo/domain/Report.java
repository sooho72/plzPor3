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
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, length = 255)
    private String status;

    @Column(nullable = false, length = 255)
    private String category;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
