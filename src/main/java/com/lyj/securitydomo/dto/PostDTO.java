package com.lyj.securitydomo.dto;


import com.lyj.securitydomo.domain.Post;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PostDTO {

    private Long postId  ; // 게시글 ID자동 생성
    @NotEmpty
    @Size(min = 3, max = 200)
    private String title ; // 제목

    @NotEmpty
    private String contentText ; //게시글본문내용저장

    private Date createdAt; //등록날짜

    private Date upDatedAt; //수정날짜

    private Long userId; // 작성자 ID

    private String username; //username

}