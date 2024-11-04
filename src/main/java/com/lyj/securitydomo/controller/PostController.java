package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.service.PostService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;;

@Controller
@Log4j2
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/list1")
    public String list1(Model model) {
        List<PostDTO> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "post/list1"; // list1.html을 지정
    }


}