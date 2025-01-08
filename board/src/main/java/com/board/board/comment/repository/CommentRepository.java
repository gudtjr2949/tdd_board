package com.board.board.comment.repository;

import com.board.board.board.repository.entity.Board;
import com.board.board.comment.repository.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByBoard(Board board);
}
