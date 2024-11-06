

package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Post;
//import com.lyj.securitydomo.domain.QpPhoto;
import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;

import java.util.List;
import java.util.stream.Collectors;

public interface PostService {
    Long register(PostDTO postDTO);

    PostDTO readOne(Long postId);

    void modify(PostDTO postDTO);

    void remove(Long postId);

    PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO);

    // 모든 게시글을 가져오는 메서드 추가
//    List<PostDTO> getAllPosts();


    default Post dtoToEntity(PostDTO postDTO) {
        Post post = Post.builder()
                .postId(postDTO.getPostId())
                .title(postDTO.getTitle())
                .contentText(postDTO.getContentText())

                .build();

        if (postDTO.getFileNames() != null) {
            postDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                post.addImage(arr[0], arr[1]);
            });
        }
        return post;
    }

    default PostDTO entityToDTO(Post post) {
        PostDTO postDTO = PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .contentText(post.getContentText())
                .createdAt(post.getCreatedAt())
                .upDatedAt(post.getUpDatedAt())
                .build();

        List<String> fileNames =
                post.getImageSet().stream().sorted().map(postImage ->
                                postImage.getUuid() + "_" + postImage.getFileName())
                        .collect(Collectors.toList());
        postDTO.setFileNames(fileNames);
        return postDTO;
    }
}


