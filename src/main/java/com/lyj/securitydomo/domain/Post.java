package com.lyj.securitydomo.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Post extends PostBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId  ; // 게시글 ID자동 생성
    private String title ; // 제목
    private String contentText ; //게시글본문내용저장

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "userId")
    private User user;


    public void change(String title, String content){
        this.title = title;
        this.contentText = content;
    }


    public Long getId() {
        return postId;
    }

    public void setCreatedAt(Date date) {

    }
}