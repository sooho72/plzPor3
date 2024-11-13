
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.domain.pPhoto;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.PostRepository;
import com.lyj.securitydomo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import static com.lyj.securitydomo.domain.QUser.user;


@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${com.lyj.securitydomo.upload.path}")
    private String uploadPath;
    private Boolean isVisible; // 게시글의 가시성 필터 추가 (null: 모든 게시글, true: 공개, false: 비공개)
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public Long register(PostDTO postDTO) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("사용자가 인증되지 않았습니다.");
        }
        String username = authentication.getName();
        log.info("현재 로그인된 사용자 이름: {}", username);

        // 사용자 찾기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // Post 엔티티 생성
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .contentText(postDTO.getContentText())
                .user(user)  // 작성자 정보 설정
                .requiredParticipants(postDTO.getRequiredParticipants())
                .status(postDTO.getStatus() != null ? Post.Status.valueOf(postDTO.getStatus()) : Post.Status.모집중)                 .lat(postDTO.getLat())
                .lng(postDTO.getLng())
                .build();

        // 파일 정보 추가
        if (postDTO.getFileNames() != null && !postDTO.getFileNames().isEmpty()) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);  // 파일 UUID와 파일 이름으로 이미지 추가
                }
            });
        } else {
            // 파일이 없을 경우 랜덤 이미지 추가
            String randomImage = pPhoto.getRandomImage();
            post.addImage(randomImage, randomImage);
            log.info("파일이 없어서 랜덤 이미지를 사용합니다: {}", randomImage);
        }

        // Post 엔티티 저장
        Long postId = postRepository.save(post).getPostId();
        log.info("게시글이 성공적으로 등록되었습니다. ID: {}", postId);

        return postId;
    }


//    private final PostRepository postRepository;
    @Override
    public PostDTO readOne(Long postId) {
        // 게시글을 조회할 때, isVisible이 true인 경우에만 반환
        // findById로 게시글을 찾고, isVisible이 true인 게시글만 필터링
        Post post = postRepository.findById(postId)

                .filter(p -> p.isVisible()) // isVisible이 true인 게시글만 필터링
                .orElseThrow(() -> new EntityNotFoundException("Post not found or post is invisible"));

// 이미지 링크 목록을 PostDTO에 추가
        List<String> originalImageLinks = post.getImageSet().stream()
                .map(pPhoto::getOriginalLink)
                .collect(Collectors.toList());

        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contentText(post.getContentText()) // 본문 내용
                .createdAt(post.getCreatedAt()) // 생성일
                .updatedAt(post.getUpDatedAt()) // 수정일
                .fileNames(post.getImageSet().stream()
                        .map(image -> image.getUuid() + "_" + image.getFileName())
                        .collect(Collectors.toList())) // 이미지 링크
                .originalImageLinks(originalImageLinks) // originalImageLinks 설정
                .requiredParticipants(post.getRequiredParticipants()) // 모집 인원
                .status(post.getStatus() != null ? post.getStatus().name() : null) // 모집 상태
                .author(post.getUser() != null ? post.getUser().getUsername() : null) // 작성자 정보
                .lat(post.getLat()) // 위도
                .lng(post.getLng()) // 경도
                .build();
    }

    @Override
    public void modify(PostDTO postDTO) {
        // Post 엔티티 조회
        Post post = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // change 메서드 호출하여 필요한 필드 수정
        post.change(
                postDTO.getTitle(),
                postDTO.getContentText(),
                postDTO.getRequiredParticipants(),
                postDTO.getStatus() != null ? Post.Status.valueOf(postDTO.getStatus()) : null,
                postDTO.getLat(),
                postDTO.getLng()
        );

        // 이미지 업데이트: 기존 이미지 유지 + 새로운 이미지 추가
        if (postDTO.getFileNames() != null && !postDTO.getFileNames().isEmpty()) {
            // 기존 이미지를 제거하지 않고 새로운 이미지를 추가
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);
                }
            });
        }

        // 수정된 Post 엔티티 저장
        postRepository.save(post);
    }

    //파일삭제
    private void removeFile(List<String> fileNames) {
        for (String fileName : fileNames) {
            try {
                Path filePath = Paths.get(uploadPath, fileName);
                Files.deleteIfExists(filePath); // 원본 파일 삭제
                log.info("Deleted file: " + filePath);

                // 썸네일 이미지 삭제 (s_ prefix가 있는 파일로 가정)
                Path thumbnailPath = Paths.get(uploadPath, "s_" + fileName);
                Files.deleteIfExists(thumbnailPath); // 썸네일 파일 삭제
                log.info("Deleted thumbnail: " + thumbnailPath);

            } catch (IOException e) {
                log.error("Error deleting file: " + fileName, e);
            }
        }
    }

    //삭제
    @Override
    public void remove(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            // 이미지 파일 삭제
            List<String> fileNames = post.getOriginalImageLinks();
            if (fileNames != null && !fileNames.isEmpty()) {
                removeFile(fileNames);
            }

            // 이미지 연관 관계 제거
            post.clearAllImages();

            log.info("=============="+postId);
            // 게시글 삭제
            postRepository.deleteById(postId);
            log.info("Deleted post with ID: " + postId);
        } else {
            log.warn("Post with ID " + postId + " does not exist.");
        }
    }


    //페이징
    // 페이징 목록 조회
    @Override
    public PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("postId");
        Page<Post> result = postRepository.searchAll(pageRequestDTO.getTypes(), pageRequestDTO.getKeyword(), pageable, pageRequestDTO.getIsVisible());

        List<PostDTO> dtoList = result.getContent().stream()
                .map(post -> PostDTO.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .contentText(post.getContentText())
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpDatedAt())
                        .fileNames(post.getImageSet().stream()
                                .map(image -> image.getUuid() + "_" + image.getFileName())
                                .collect(Collectors.toList()))
                        .requiredParticipants(post.getRequiredParticipants())
                        .status(post.getStatus() != null ? post.getStatus().name() : null)
                        .author(post.getUser() != null ? post.getUser().getUsername() : null)
                        .build()
                )
                .collect(Collectors.toList());

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    public void makePostInvisible(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // 게시글을 비공개 처리 (isVisible을 false로 설정)
        post.makeInvisible();
        postRepository.save(post);  // 변경 사항 저장
    }

    @Override
    public void makePostVisible(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        post.setIsVisible(true);  // 게시글을 공개로 설정
        postRepository.save(post); // 변경된 게시글을 저장
    }

    @Override
    public PageResponseDTO<PostDTO> writinglist(PageRequestDTO pageRequestDTO, User user) {

        Pageable pageable = pageRequestDTO.getPageable("postId");

        // isVisible이 true인 게시글만 조회
        Page<Post> result = postRepository.findByUsername(user.getUsername(),pageable);  // isVisible이 true인 게시글만 조회

        // 조회된 게시글 수 로그로 출력
//        log.info("조회된 게시글 수: {}", result.getTotalElements());

        // 게시글 정보를 PostDTO로 변환하여 리스트에 담음
        List<PostDTO> dtoList = result.getContent().stream()
                .map(post -> PostDTO.builder()
                        .postId(post.getPostId()) // 게시글 ID
                        .title(post.getTitle()) // 제목
                        .contentText(post.getContentText()) // 본문 내용
                        .createdAt(post.getCreatedAt()) // 생성일
                        .updatedAt(post.getUpDatedAt()) // 수정일
                        .fileNames(post.getImageSet().stream() // 첨부된 파일들의 링크
                                .map(image -> image.getUuid() + "_" + image.getFileName())
                                .collect(Collectors.toList()))
                        .requiredParticipants(post.getRequiredParticipants()) // 모집 인원
                        .status(post.getStatus() != null ? post.getStatus().name() : null) // 모집 상태
                        .author(post.getUser() != null ? post.getUser().getUsername() : null) // 작성자 정보
                        .build()
                )
                .collect(Collectors.toList()); // PostDTO 리스트로 변환

        // 게시글 DTO 리스트와 함께 페이징 처리된 결과 반환
//        log.info("게시글 DTO 리스트: {}", dtoList); // 반환되는 게시글 리스트 확인용 로그

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO) // 페이지 요청 정보
                .dtoList(dtoList) // 게시글 DTO 리스트
                .total((int) result.getTotalElements()) // 총 게시글 수
                .build(); // PageResponseDTO 반환
    }



}
