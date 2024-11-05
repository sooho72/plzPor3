package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @PostMapping("/create")
    public ResponseEntity<String> createReport(@RequestParam Long postId,
                                               @RequestParam Long userId,
                                               @RequestParam String category,
                                               @RequestParam String reason) {
        // 로그 찍기
        logger.info("신고 생성 요청: postId={}, userId={}, category={}, reason={}", postId, userId, category, reason);

        reportService.createReport(postId, userId, category, reason);
        logger.info("신고가 성공적으로 접수되었습니다.");

        return ResponseEntity.ok("신고가 접수되었습니다."); // 여기서 문자열 응답
    }
}