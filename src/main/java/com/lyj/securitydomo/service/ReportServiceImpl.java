package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    private final ModelMapper modelMapper; // 모델 매퍼 필드

    @Override
    public void createReport(ReportDTO reportDTO) {
        log.info("신고 생성 요청: postId={}, category={}, reason={}", reportDTO.getPostId(), reportDTO.getCategory(), reportDTO.getReason());

        Post post = postRepository.findById(reportDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        Report report = Report.builder()
                .post(post)
                .category(Report.ReportCategory.valueOf(reportDTO.getCategory().toUpperCase()))
                .reason(reportDTO.getReason())
                .status(Report.ReportStatus.PENDING)
                .createdAt(new Date())
                .build();

        reportRepository.save(report);
        log.info("신고가 데이터베이스에 저장되었습니다: {}", report);
    }

    @Override
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        if (reports.isEmpty()) {
            log.warn("No reports found.");
        }

        List<ReportDTO> reportDTOList = reports.stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());

        log.info("All Reports: {}", reportDTOList); // 변환된 ReportDTO 리스트 로그 출력
        return reportDTOList;
    }

    @Override
    public List<ReportDTO> getReportsInProgress() {
        List<Report> reports = reportRepository.findByStatus(Report.ReportStatus.PENDING);
        if (reports.isEmpty()) {
            log.warn("No reports in progress.");
        }

        List<ReportDTO> reportDTOList = reports.stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());

        log.info("Reports in Progress: {}", reportDTOList); // 변환된 ReportDTO 리스트 로그 출력
        return reportDTOList;
    }

    @Override
    public List<ReportDTO> getReportsByPostId(Long postId) {
        List<Report> reports = reportRepository.findByPost_PostId(postId); // 특정 postId에 대한 신고 목록을 가져옴
        if (reports.isEmpty()) {
            log.warn("No reports found for postId: {}", postId);
        }

        return reports.stream()
                .map(report -> modelMapper.map(report, ReportDTO.class))
                .collect(Collectors.toList());
    }
}