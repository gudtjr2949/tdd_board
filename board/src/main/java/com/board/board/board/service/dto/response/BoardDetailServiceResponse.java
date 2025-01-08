package com.board.board.board.service.dto.response;

import com.board.board.board.repository.entity.Board;
import com.board.board.comment.repository.entity.Comment;
import com.board.board.comment.service.dto.response.CommentDetailServiceResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BoardDetailServiceResponse {
    private Long boardId;
    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private Long views;
    private List<CommentDetailServiceResponse> comments;

    @Builder
    public BoardDetailServiceResponse(Board board, List<Comment> comments) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.nickname = board.getMember().getNickname();
        this.createdAt = board.getCreatedAt();
        this.views = board.getViews();
        this.comments = comments.stream()
                .map(comment -> CommentDetailServiceResponse.builder()
                        .commentId(comment.getId())
                        .nickname(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }
}
