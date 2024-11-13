package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/report")
@Log4j2
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

//
@PostMapping("/create")
//@RequestBody, @RequestParam:요청 파라미터
//(@RequestParam Long id String name) 형태 -> "/create?name="홍길동"&id=hkd 하나하나씩보낸다
//(@RequestBody ReportDTO) 몸통으로 전체를 받겟다,insert일때, form으로 통째로)
//JSON타입으로 받아서 ReportDTO에서 객체로 받음
public ResponseEntity<String> createReport(@RequestBody ReportDTO reportDTO) {
    // 서비스에서 신고를 생성하는 메서드 호출
    reportService.createReport(reportDTO);
    log.info("신고가 성공적으로 접수되었습니다. postId={}, category={}, reason={}", reportDTO.getPostId(), reportDTO.getCategory(), reportDTO.getReason());

    // 신고 접수 성공 후 응답 반환
    return ResponseEntity.ok("신고가 접수되었습니다.");
}

//    @GetMapping("/list")
//    public ResponseEntity<List<ReportDTO>> getReports() {
//        try {
//            List<ReportDTO> reports = reportService.getAllReports(); // 모든 신고 리스트 가져오기
//            if (reports.isEmpty()) {
//                return ResponseEntity.noContent().build(); // 신고 리스트가 비어있을 경우 204 반환
//            }
//            return ResponseEntity.ok(reports); // 조회된 리스트 반환
//        } catch (Exception e) {
//            log.error("Error retrieving report list", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 오류 반환
//        }
//    }

}