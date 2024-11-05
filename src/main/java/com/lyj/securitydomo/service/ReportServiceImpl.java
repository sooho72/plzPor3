package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Date;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createReport(Long postId, Long userId, String category, String reason) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 열거형으로 변환하여 설정
        Report.ReportCategory reportCategory = Report.ReportCategory.valueOf(category.toUpperCase());

        Report report = Report.builder()
                .post(post)
                .user(user)
                .category(reportCategory) // 변환한 열거형 사용
                .reason(reason)
                .status(Report.ReportStatus.PENDING) // 상태 설정
                .createdAt(new Date())
                .build();

        reportRepository.save(report); // 데이터베이스에 저장
    }

    @Override
    public List<Report> findAllReports() {
        return reportRepository.findAll(); // 모든 신고를 가져옴
    }
}