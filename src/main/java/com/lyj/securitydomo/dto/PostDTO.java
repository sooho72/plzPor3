package com.lyj.securitydomo.dto;

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

    private Long postId; // 게시글 ID 자동 생성

    @NotEmpty
    @Size(min = 3, max = 200)
    private String title; // 제목

    @NotEmpty
    private String contentText; // 게시글 본문 내용 저장

    private Date createdAt; // 등록 날짜

    private Date upDatedAt; // 수정 날짜

    private List<String> fileNames; // 파일 이름 리스트

    private Integer requiredParticipants; // 모집 인원

    private String status; // 모집 상태 (모집중 또는 모집완료)
}