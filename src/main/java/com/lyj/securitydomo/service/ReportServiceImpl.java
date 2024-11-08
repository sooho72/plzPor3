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
        List<Report> reports = reportRepository.findAll(); // 모든 신고를 가져옴
        return reports.stream()
                .map(report -> modelMapper.map(report, ReportDTO.class)) // Report 엔티티를 ReportDTO로 변환
                .collect(Collectors.toList());
    }
}