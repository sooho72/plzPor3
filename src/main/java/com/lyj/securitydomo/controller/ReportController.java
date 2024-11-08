package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.service.ReportService;
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
    // 서비스에서 신고를 생성하는 메서드를 호출
    reportService.createReport(reportDTO);
//        logger.info("신고가 성공적으로 접수되었습니다.");

    return ResponseEntity.ok("신고가 접수되었습니다."); // 여기서 문자열 응답
}

    // 신고 리스트 조회를 위한 엔드포인트
    @GetMapping("/list")
    public ResponseEntity<List<ReportDTO>> getReports() {
        List<ReportDTO> reports = reportService.getAllReports(); // 모든 신고 리스트 가져오기
        return ResponseEntity.ok(reports); // 조회된 리스트 반환
    }

}