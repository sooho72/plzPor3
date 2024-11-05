package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Report;
import java.util.List;

public interface ReportService {
    void createReport(Long postId, Long userId, String category, String reason);
    List<Report> findAllReports(); // Method to fetch all reports
}