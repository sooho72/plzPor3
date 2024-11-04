package com.lyj.securitydomo.repository;


import com.lyj.securitydomo.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}