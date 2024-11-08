
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.pPhoto;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Override
    public Long register(PostDTO postDTO) {
        // 1. Post 엔티티 생성
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .contentText(postDTO.getContentText())
                .requiredParticipants(postDTO.getRequiredParticipants())
                .status(postDTO.getStatus() != null ? Post.Status.valueOf(postDTO.getStatus()) : null)
                .lat(postDTO.getLat()) // 위도 추가
                .lng(postDTO.getLng()) // 경도 추가
                .build();

        // 2. 파일 정보 추가 (파일이 존재하는 경우에만 추가)
        if (postDTO.getFileNames() != null && !postDTO.getFileNames().isEmpty()) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);
                }
            });
        } else {
            // 파일이 없을 경우 랜덤 이미지 추가
            String randomImage = pPhoto.getRandomImage(); // pPhoto에서 랜덤 이미지 가져오기
            post.addImage(randomImage, randomImage); // 랜덤 이미지를 파일 이름으로 사용
            log.info("파일이 없어서 랜덤 이미지를 사용합니다: {}", randomImage); // 로그 추가

        }
        // 3. Post 엔티티를 저장 (연관된 pPhoto 엔티티도 함께 저장됨)
        Long postId = postRepository.save(post).getPostId();
        log.info("게시글이 성공적으로 등록되었습니다. ID: {}", postId);

        return postId;
    }


    private final PostRepository postRepository;
    @Override
    public PostDTO readOne(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();

        return PostDTO.builder()
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
                .build();
    }

    @Override
    public void modify(PostDTO postDTO) {
        Post post = postRepository.findById(postDTO.getPostId()).orElseThrow();

        post.change(postDTO.getTitle(), postDTO.getContentText());
        post.clearAllImages();

        if (postDTO.getFileNames() != null) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);
                }
            });
        }
        postRepository.save(post);
    }

    @Override
    public void remove(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("postId");

        Page<Post> result = postRepository.searchAll(types, keyword, pageable);

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
}
