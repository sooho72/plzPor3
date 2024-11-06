package com.lyj.securitydomo.service;

import groovy.util.logging.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class PostServiceTest {
    private static final Logger log = LogManager.getLogger(PostServiceTest.class);
    @Autowired
    private PostService postService;
    @Test
    public void testRegister() {
        log.info(postService.getClass().getName());
    }

}