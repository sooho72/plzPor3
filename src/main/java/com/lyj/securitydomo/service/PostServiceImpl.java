package com.lyj.securitydomo.service;

import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.PostRepository;
import groovy.transform.ASTTest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;

    @Override
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Long register(PostDTO postDTO) {
        Post post = modelMapper.map(postDTO, Post.class);
        Long postId = postRepository.save(post).getPostId();
        return postId;
    }

    @Override
    public PostDTO readOne(Long postId) {
        Optional<Post> result = postRepository.findById(postId);
        Post post = result.orElseThrow();
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        return postDTO;
    }

    @Override
    public void modify(PostDTO postDTO) {
        Optional<Post> result = postRepository.findById(postDTO.getPostId());
        Post post = result.orElseThrow();
        post.change(postDTO.getTitle(), postDTO.getContentText());
        postRepository.save(post);
    }

    @Override
    public void remove(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes() != null ? pageRequestDTO.getTypes() : new String[0];
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("postId");

        Page<Post> result = postRepository.searchAll(Arrays.asList(types), keyword, pageable);

        List<PostDTO> dtoList = result.getContent().stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());

        if (dtoList.isEmpty()) {
            log.info("dtoList is empty.");
        } else {
            log.info("dtoList: {}", dtoList);
        }

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }


}