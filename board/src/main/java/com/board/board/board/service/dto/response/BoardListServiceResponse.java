package com.board.board.board.service.dto.response;

import com.board.board.board.repository.entity.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BoardListServiceResponse {
    private Long boardId;
    private String title;
    private String writer;
    private Long views;
    private Long comments;

    @Builder
    private BoardListServiceResponse(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.writer = board.getMember().getNickname();
        this.views = board.getViews();
        this.comments = board.getComments() == null ? 0L : (long) board.getComments().size();
    }
}
