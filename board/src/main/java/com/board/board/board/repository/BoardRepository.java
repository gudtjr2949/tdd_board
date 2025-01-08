package com.board.board.board.repository;

import com.board.board.board.repository.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
//    Page<Board> findAll(Pageable pageable);
    @Query(value = "SELECT b FROM Board b")
    List<Board> findBoardWithOutCount(Pageable pageable);

    List<Board> findByIdLessThanOrderByIdDesc(Long boardId, Pageable pageable);

    Page<Board> findBoardByTitleContaining(String title, Pageable pageable);

    List<Board> findBoardByTitleContainingAndIdLessThanOrderByIdDesc(String title, Long boardId, Pageable pageable);

    Page<Board> findBoardByMemberNicknameContaining(String nickname, Pageable pageable);

    List<Board> findBoardByMemberNicknameContainingAndIdLessThanOrderByIdDesc(String nickname, Long boardId, Pageable pageable);
}
