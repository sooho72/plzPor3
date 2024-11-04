
package com.lyj.securitydomo.service;

import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;

import java.util.List;

public interface PostService {
    Long register(PostDTO postDTO);

    PostDTO readOne(Long postId);

    void modify(PostDTO postDTO);

    void remove(Long postId);

    PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO);

    // 모든 게시글을 가져오는 메서드 추가
    List<PostDTO> getAllPosts();

    }

