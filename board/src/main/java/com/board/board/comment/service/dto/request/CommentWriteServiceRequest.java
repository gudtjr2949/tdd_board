package com.board.board.comment.service.dto.request;

import com.board.board.board.repository.entity.Board;
import com.board.board.comment.repository.entity.Comment;
import com.board.board.member.repository.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentWriteServiceRequest {
    private String email;
    private Long boardId;
    private String content;


    public Comment toComment(Member member, Board board) {
        return Comment.builder()
                .member(member)
                .board(board)
                .content(content)
                .build();
    }
}
