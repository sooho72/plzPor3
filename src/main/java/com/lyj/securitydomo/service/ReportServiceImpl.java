package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Date;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;
    @Qualifier("conversionServicePostProcessor")
    @Autowired
    private BeanFactoryPostProcessor conversionServicePostProcessor;


    @Override
    public void createReport(ReportDTO reportDTO) {
        logger.info("신고 생성 요청: postId={}, userId={}, category={}, reason={}",
                reportDTO.getPostId(), reportDTO.getUserId(), reportDTO.getCategory(), reportDTO.getReason());

        Post post = postRepository.findById(Long.valueOf(reportDTO.getPostId()))
                .orElseThrow(() -> {
                    logger.error("해당 게시글을 찾을 수 없습니다: postId={}", reportDTO.getPostId());
                    return new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
                });

        User user = userRepository.findById(Long.valueOf(reportDTO.getUserId()))
                .orElseThrow(() -> {
                    logger.error("해당 사용자를 찾을 수 없습니다: userId={}", reportDTO.getUserId());
                    return new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
                });

        // 열거형으로 변환하여 설정
        Report.ReportCategory reportCategory = Report.ReportCategory.valueOf(reportDTO.getCategory().toUpperCase());

        Report report = Report.builder()
                .post(post)
                .user(user)
                .category(reportCategory)
                .reason(reportDTO.getReason())
                .status(Report.ReportStatus.PENDING)
                .createdAt(new Date())
                .build();

        reportRepository.save(report);
        logger.info("신고가 데이터베이스에 저장되었습니다: {}", report);
    }



    @Override
    public List<Report> findAllReports() {
        return reportRepository.findAll(); // 모든 신고를 가져옴
    }
}