package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Board;
import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public void insert(Board board, User user) {
        board.setUser(user);
        boardRepository.save(board);

    }

    @Override
    public List<Board> list() {
        return boardRepository.findAll();
    }

    @Override
    public Board findById(Long num) {
        Board board = boardRepository.findById(num).get();
        board.setHitcount(board.getHitcount()+1);
        boardRepository.save(board);// 조회수 증가
        return board;
    }

    @Override
    public void update(Board board) { // 게시글 수정
        Board oldBoard = boardRepository.findById(board.getNum()).get();
        oldBoard.setTitle(board.getTitle());
        oldBoard.setContent(board.getContent());
        boardRepository.save(oldBoard); // 수정된 게시글 저장
    }

    @Override
    public void delete(Long num) {
        boardRepository.deleteById(num);

    }
}
