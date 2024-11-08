package com.lyj.securitydomo.service;

import com.lyj.securitydomo.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    void createReport(ReportDTO reportDTO);
    List<ReportDTO> getAllReports(); // 모든 신고를 가져오는 메서드
}