package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;


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

        post.change(postDTO.getTitle(),postDTO.getContentText());

        postRepository.save(post);

    }

    @Override
    public void remove(Long postId) {
        postRepository.deleteById(postId);

    }

}