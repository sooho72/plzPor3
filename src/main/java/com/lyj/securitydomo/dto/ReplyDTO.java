package com.lyj.securitydomo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReplyDTO {
    private Long replyId;
    private Long postId;
    private String username;
    private String content;
    private LocalDateTime regDate;
    private Long parentId;  // 부모 댓글 ID
}