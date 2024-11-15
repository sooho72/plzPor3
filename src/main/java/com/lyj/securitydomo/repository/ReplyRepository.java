package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostAndParentIsNull(Post post);// 특정 보드의 최상위 댓글만 가져오기
    //특정 게시글의 댓글조회


}