package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.PostService;
import com.lyj.securitydomo.service.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@Log4j2
public class AdminController {

    private final ReportService reportService;
    private final PostService postService; // 추가: PostService를 주입 받아야 함

    // 생성자 주입
    public AdminController(ReportService reportService, PostService postService) {
        this.reportService = reportService;
        this.postService = postService; // 추가된 부분
    }

    /**
     * 관리자 메인 페이지로 이동
     */
    @GetMapping("/adminIndex")
    public String adminIndex(Model model) {
        return "admin/adminIndex";
    }

    /**
     * 신고글 목록 조회 (처리 중인 글 또는 전체 목록)
     * @param filter - 필터 조건 ("all" 또는 "in-progress")
     */
    @GetMapping("/reports")
    public String getReports(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter, Model model) {
        List<ReportDTO> reportList;

        if ("in-progress".equals(filter)) {
            reportList = reportService.getReportsInProgress();
        } else {
            reportList = reportService.getAllReports();
        }

        model.addAttribute("reportList", reportList);
        model.addAttribute("filter", filter);
        return "admin/reportList";
    }

    /**
     * 특정 게시글에 대한 신고 목록 및 신고 횟수 조회
     * @param postId - 신고된 게시글 ID
     */
    @GetMapping("/reports/{postId}")
    public String getReportsByPostId(@PathVariable Long postId, Model model) {
        List<ReportDTO> reports = reportService.getReportsByPostId(postId);
        int reportCount = reportService.countReportsByPostId(postId);
        model.addAttribute("reports", reports);
        model.addAttribute("reportCount", reportCount);
        return "redirect:/post/read/" + postId;
    }

    /**
     * 신고글 비공개 처리
     * @param reportId - 비공개할 신고 ID
     */
    @PostMapping("/reports/{reportId}/hide")
    public String hideReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        // 신고 처리
        reportService.markAsHidden(reportId);

        // 신고된 게시글을 비공개로 설정
        Long postId = reportService.getPostIdByReportId(reportId); // Report 엔티티에서 해당 게시글 ID를 가져옴
        postService.makePostInvisible(postId); // 게시글 비공개 처리

        redirectAttributes.addFlashAttribute("message", "신고글과 게시글이 비공개 처리되었습니다.");
        return "redirect:/admin/reports";
    }

    /**
     * 신고글 공개 처리
     * @param reportId - 공개할 신고 ID
     */
    @PostMapping("/reports/{reportId}/reveal")
    public String revealReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        // 신고 처리
        reportService.markAsVisible(reportId);

        // 신고된 게시글을 공개로 설정
        Long postId = reportService.getPostIdByReportId(reportId); // Report 엔티티에서 해당 게시글 ID를 가져옴
        postService.makePostVisible(postId); // 게시글 공개 처리

        redirectAttributes.addFlashAttribute("message", "신고글과 게시글이 공개 처리되었습니다.");
        return "redirect:/admin/reports";
    }

    /**
     * 신고글 상태 토글 처리
     * @param reportId - 상태를 변경할 신고 ID
     * @param request - 요청 본문에서 `visible` 키로 상태 결정
     */
    @PostMapping("/reports/{reportId}/toggle-visibility")
    public ResponseEntity<Void> toggleVisibility(@PathVariable Long reportId, @RequestBody Map<String, Boolean> request) {
        Boolean isVisible = request.get("visible");
        if (isVisible != null) {
            if (isVisible) {
                reportService.markAsVisible(reportId);
            } else {
                reportService.markAsHidden(reportId);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}