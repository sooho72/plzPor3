package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.dto.PostDTO;

import java.util.List;

public interface PostService {
    Long register(PostDTO postDTO);

    PostDTO readOne(Long postId);

    void modify(PostDTO postDTO);

    void remove(Long postId);


}