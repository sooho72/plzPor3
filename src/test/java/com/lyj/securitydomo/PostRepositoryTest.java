package com.lyj.securitydomo;

import com.lyj.securitydomo.domain.Post;
import com.lyj.securitydomo.repository.PostRepository;


import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.IntStream;


@SpringBootTest
@Log4j2
public class PostRepositoryTest {

    private static final Logger log = LogManager.getLogger(PostRepositoryTest.class);
    @Autowired
    private PostRepository postRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Post post = Post.builder()
                    .title("title" + i)
                    .contentText("content" + i)
                    .build();

            Post result = postRepository.save(post);
            log.info(result);

        });

    }

    @Test
    public void testSelect() {
        Long postId = 100L;

        Optional<Post> result = postRepository.findById(postId);

        Post post = result.orElseThrow();
        log.info(post);


    }

}