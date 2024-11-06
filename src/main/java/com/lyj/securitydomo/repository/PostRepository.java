package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
//검색
    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE " +
            "(:keyword IS NULL OR p.title LIKE %:keyword% OR p.contentText LIKE %:keyword%) " +
            "AND ((:types) IS NULL OR p.title IN :types)")
    Page<Post> searchAll(@Param("types") List<String> types,
                         @Param("keyword") String keyword,
                         Pageable pageable);

//    @Query("SELECT p FROM Post p JOIN FETCH p.user")
//    List<Post> findAllWithUser();
//
//    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.postId = :postId")
//    Optional<Post> findByIdWithUser(@Param("postId") Long postId);

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select p from Post p where p.postId=:postId")
    Optional<Post> findByIdWithImages(Long postId);
}