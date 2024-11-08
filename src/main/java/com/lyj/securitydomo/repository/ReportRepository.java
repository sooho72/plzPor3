package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 모든 신고를 가져오는 메서드 정의 (기본 제공 메서드)
    List<Report> findAll();
}