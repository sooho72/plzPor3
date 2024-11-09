package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // 기본 관리자 페이지
    @GetMapping("/adminIndex")
    public String adminIndex(Model model) {

        return "admin/adminIndex"; // adminIndex.html로 이동
    }


@GetMapping("/reports")
public String getReports(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter, Model model) {
    List<ReportDTO> reportList;

    if ("in-progress".equals(filter)) {
        reportList = reportService.getReportsInProgress();  // 처리 중인 신고글만 조회
    } else {
        reportList = reportService.getAllReports();  // 모든 신고글 조회
    }

    model.addAttribute("reportList", reportList);
    model.addAttribute("filter", filter);
    return "admin/reportList";  // admin/reportList.html로 이동
}
//상세보기
    @GetMapping("/admin/reports/{postId}")
    public String getReportsByPostId(@PathVariable Long postId, Model model) {
        List<ReportDTO> reports = reportService.getReportsByPostId(postId); // 서비스에서 해당 postId에 대한 신고들 조회
        model.addAttribute("reports", reports); // 뷰에 리포트 목록 전달
        // 해당 게시글의 상세 보기 페이지로 리다이렉션
        return "redirect:/post/read/" + postId; // postId에 해당하는 read.html로 리다이렉트
    }

    // 신고글 처리 (PENDING 상태에서 완료 처리 및 유저에게 보이지 않게 처리)
    @PostMapping("/reports/{reportId}/process")
    public String processReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportService.markReportAsCompleted(reportId);  // 상태를 COMPLETED로 변경하고 유저에게 보이지 않게 처리
        redirectAttributes.addFlashAttribute("message", "신고글이 처리되었습니다.");
        return "redirect:/admin/reports";  // 처리 후 목록 페이지로 리다이렉트
    }

    // 신고글 숨기기 (삭제가 아닌 유저에게 보이지 않게 처리)
    @PostMapping("/reports/{reportId}/hide")
    public String hideReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportService.hideReport(reportId);  // 신고글을 유저에게 보이지 않게 처리
        redirectAttributes.addFlashAttribute("message", "신고글이 숨겨졌습니다.");
        return "redirect:/admin/reports";  // 숨기기 후 목록 페이지로 리다이렉트
    }
}