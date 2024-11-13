package com.lyj.securitydomo.repository.search;

import com.lyj.securitydomo.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostSearch {

    /**
     * 검색 메서드: 게시글을 조건에 따라 검색합니다.
     *
     * @param types 검색 타입 배열 (예: 제목, 내용 등)
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @param isVisible 게시글의 가시성 필터 (null: 모든 게시글, true: 공개 게시글, false: 비공개 게시글)
     * @return 조건에 맞는 게시글 페이지 결과
     */
    Page<Post> searchAll(String[] types, String keyword, Pageable pageable, Boolean isVisible);
}