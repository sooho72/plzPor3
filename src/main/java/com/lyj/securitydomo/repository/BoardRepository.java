package com.lyj.securitydomo.repository;

import com.lyj.securitydomo.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
