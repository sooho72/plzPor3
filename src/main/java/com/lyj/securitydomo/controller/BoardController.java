package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.config.auth.PrincipalDetails;
import com.lyj.securitydomo.domain.Board;
import com.lyj.securitydomo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/insert")
    public String insert() {

        return "board/register";
    }
    @PostMapping("/insert")
    public String insert(Board board,
                         @AuthenticationPrincipal PrincipalDetails principalDetails) {
        boardService.insert(board, principalDetails.getUser()); //principalDetails를 통해 로그인한 사용자 정보를 가져옴(권한 정보 포함)
        return "redirect:/board/list";
    }
    @GetMapping("/view")
    public String view(@RequestParam Long num, Model model) {
        model.addAttribute("board", boardService.findById(num));
        return "/board/view";
    }
    //수정폼
    @GetMapping("/modify")
    public String update(@RequestParam Long num, Model model) {
        model.addAttribute("board", boardService.findById(num));
        return "/board/modify";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("lists", boardService.list());
        return "/board/list";
    }
    @PutMapping("/update")
    public String update(Board board) {
        boardService.update(board);
        return "redirect:/board/view?num="+board.getNum();
    }
    //삭제
    @GetMapping("/delete")
    public String delete(@RequestParam Long num) {
        boardService.delete(num);
        return "redirect:/board/list";
    }
}