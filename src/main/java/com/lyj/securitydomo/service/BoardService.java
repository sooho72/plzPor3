package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.Board;
import com.lyj.securitydomo.domain.User;

import java.util.List;

public interface BoardService {
    void insert(Board board, User user);
    public List<Board> list();
    public Board findById(Long num);
    public void update(Board board);
    public void delete(Long num);
}