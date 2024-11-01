package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.Board;
import com.lyj.securitydomo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/insert")
    public String insert() {
        return "board/insert";
    }
    @PostMapping("/insert")
    public String insert(Board board, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        boardService.insert(board, principalDetails.getUser()); //principalDetails를 통해 로그인한 사용자 정보를 가져옴(권한 정보 포함)
        return "redirect:/board/list";
    }
}