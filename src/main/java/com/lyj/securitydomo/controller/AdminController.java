package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@Log4j2
public class AdminController {

    private final ReportService reportService; // ReportService 주입

    // 생성자 주입
    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping // 기본 관리자 페이지
    public String adminIndex(Model model) {
        // 신고 글 리스트 가져오기
        List<ReportDTO> reportList = reportService.getAllReports();
        model.addAttribute("reportList", reportList);
        log.info("신고리스트"+reportList);
        return "admin/AdminIndex"; // AdminIndex.html로 이동
    }
}