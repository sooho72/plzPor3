//	•	목적: 비즈니스 로직을 담당하고, 여러 레포지토리를 조합하여 작업을 처리
//	•	기능: 레포지토리와 같은 하위 계층을 호출하고, 필요한 비즈니스 로직을 추가
//	•	구체적 구현: 서비스 계층에서는 하나 이상의 레포지토리나 다른 서비스를 조합하여 더 복잡한 작업을 수행
//	예를 들어, 사용자 등록 시 유효성 검사 후 데이터를 저장하는 등의 로직을 추가
//	•	예시: createReport(), getReports(), getReportsInProgress() 등의 비즈니스 로직을 처리하는 메서드를 구현
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    void createReport(ReportDTO reportDTO);//신고 생성

    List<ReportDTO> getAllReports(); // 모든 신고를 가져오는 메서드

    List<ReportDTO> getReportsInProgress();//처리중인 신고를 가져오는 메서드

    List<ReportDTO> getReportsByPostId(Long postId); // 특정 postId에 대한 신고 목록 가져오는 메서드 추가

    void hideReport(Long reportId); // 신고글을 숨기는 메서드 (삭제 아님)

    void markReportAsCompleted(Long reportId); // 상태를 COMPLETED로 변경 후 숨김
}