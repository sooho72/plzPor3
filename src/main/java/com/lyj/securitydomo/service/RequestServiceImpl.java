package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Report;
import com.lyj.securitydomo.domain.Request;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.ReportDTO;
import com.lyj.securitydomo.dto.RequestDTO;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.ReportRepository;
import com.lyj.securitydomo.repository.RequestRepository;
import com.lyj.securitydomo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RequestServiceImpl는 RequestService 인터페이스를 구현하며,
 * 요청 관련 비즈니스 로직을 처리합니다.
 */
@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository; // ReportRepository 의존성 주입
    private final PostRepository postRepository; // PostRepository 의존성 주입
    private final ModelMapper modelMapper; // Entity-DTO 간 변환을 위한 ModelMapper 의존성 주입
    //신청 생성
    @Override
    public void createRequest(RequestDTO requestDTO) {
        log.info("신청 생성 요청: postId={}, message={}", requestDTO.getPostId(),  requestDTO.getContent());

        // 신청할 게시글(Post)을 조회
        Post post = postRepository.findById(requestDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // Request 엔티티 생성 및 초기화
        Request request = Request.builder()
                .post(post) // 신청할 게시글 설정
                .content(requestDTO.getContent()) // 신청 사유 설정
                .status(Request.RequestStatus.PENDING) // 초기 상태를 PENDING으로 설정
                .regDate(new Date()) // 생성 날짜 설정
                .build();

        // 생성된 Report 엔티티를 데이터베이스에 저장
        requestRepository.save(request);
        log.info("신청이 데이터베이스에 저장되었습니다: {}", request);
    }

    //특정 게사물에 대한 요청 목록 조회
    @Override
    public List<RequestDTO> getRequestsByPostId(Long postId) {
// 특정 postId에 대한 모든 Request 엔티티를 조회
        List<Request> requests = requestRepository.findByPost_PostId(postId);
        return requests.stream()
                .map(this::convertToRequestDTO) // Report 엔티티를 ReportDTO로 변환
                .collect(Collectors.toList());    }

    //특정 사용자가 신청한 요청 목록 조회
    @Override
    public List<RequestDTO> getRequestsByUserId(Long userId) {
        return List.of();
    }

    //요청 삭제
    @Override
    public void deleteRequest(Long requestId) {

    }
    private RequestDTO convertToRequestDTO(Request request) {
        RequestDTO requestDTO = modelMapper.map(request, RequestDTO.class);
        requestDTO.setPostTitle(request.getPost().getTitle()); // post의 title을 RequestDTO에 설정
        return requestDTO;
    }
}