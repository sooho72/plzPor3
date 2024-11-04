package com.lyj.securitydomo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReportStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동증가
    private Integer reportStatusId;

    @Column(nullable = false, length = 20)
    private String statusName;
}