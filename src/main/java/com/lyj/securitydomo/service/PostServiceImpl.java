package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Override
    public Long register(PostDTO postDTO) {
        Post post = dtoToEntity(postDTO);
        return postRepository.save(post).getPostId();
    }

    @Override
    public PostDTO readOne(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        return entityToDTO(post);
    }

    @Override
    public void modify(PostDTO postDTO) {
        Post post = postRepository.findById(postDTO.getPostId()).orElseThrow();

        post.change(postDTO.getTitle(), postDTO.getContentText());
        post.clearAllImages();

        if (postDTO.getFileNames() != null) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                post.addImage(split[0], split[1]);
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
        String[] types = Optional.ofNullable(pageRequestDTO.getTypes()).orElse(new String[0]);
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("postId");

        Page<Post> result = postRepository.searchAll(List.of(types), keyword, pageable);
        log.info("서비스 결과: " + result);

        List<PostDTO> dtoList = result.getContent().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        dtoList.forEach(dto -> log.info("DTO: " + dto));

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    // entity를 DTO로 변환하는 메서드
    private PostDTO entityToDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contentText(post.getContentText())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpDatedAt())
                .fileNames(post.getImageSet().stream().map(image -> image.getUuid() + "_" + image.getFileName()).collect(Collectors.toList()))
                .requiredParticipants(post.getRequiredParticipants())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .author(post.getUser() != null ? post.getUser().getUsername() : null)
                .build();
    }

    // DTO를 entity로 변환하는 메서드
    private Post dtoToEntity(PostDTO postDTO) {
        Post post = Post.builder()
                .postId(postDTO.getPostId())
                .title(postDTO.getTitle())
                .contentText(postDTO.getContentText())
                .requiredParticipants(postDTO.getRequiredParticipants())
                .status(postDTO.getStatus() != null ? Post.Status.valueOf(postDTO.getStatus()) : null)
                .build();

        if (postDTO.getFileNames() != null) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] split = fileName.split("_");
                if (split.length == 2) {
                    post.addImage(split[0], split[1]);
                }
            });
        }
        return post;
    }
}