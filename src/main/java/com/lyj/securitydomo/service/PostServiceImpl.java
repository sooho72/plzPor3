package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    private final PostRepository postRepository;

    @Override
    public Long register(PostDTO postDTO) {
        Post post = Post.builder()
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
        return postRepository.save(post).getPostId();
    }

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
                .map(post -> modelMapper.map(post, PostDTO.class)).collect(Collectors.toList());


        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }
}
//        String[] types = Optional.ofNullable(pageRequestDTO.getTypes()).orElse(new String[0]);
//        String keyword = pageRequestDTO.getKeyword();
//        Pageable pageable = pageRequestDTO.getPageable("postId");
//
//        Page<Post> result = postRepository.searchAll(List.of(types), keyword, pageable);
//        log.info("서비스 결과: " + result);

//        // Entity에서 직접 DTO로 변환
//        List<PostDTO> dtoList = result.getContent().stream()
//                .map(post -> PostDTO.builder()
//                        .postId(post.getPostId())
//                        .title(post.getTitle())
//                        .contentText(post.getContentText())
//                        .createdAt(post.getCreatedAt())
//                        .updatedAt(post.getUpDatedAt())
//                        .fileNames(post.getImageSet().stream()
//                                .map(image -> image.getUuid() + "_" + image.getFileName())
//                                .collect(Collectors.toList()))
//                        .requiredParticipants(post.getRequiredParticipants())
//                        .status(post.getStatus() != null ? post.getStatus().name() : null)
//                        .author(post.getUser() != null ? post.getUser().getUsername() : null)
//                        .build()
//                )
//                .collect(Collectors.toList());

//        // PageResponseDTO에 dtoList와 total 전달
//        return PageResponseDTO.<PostDTO>builder()
//                .pageRequestDTO(pageRequestDTO)
//                .dtoList(dtoList) // dtoList 변수 전달
//                .total((int) result.getTotalElements())
//                .build();
//    }
