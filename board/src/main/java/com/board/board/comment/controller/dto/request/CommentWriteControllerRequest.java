package com.board.board.comment.controller.dto.request;

import com.board.board.comment.service.dto.request.CommentWriteServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentWriteControllerRequest {

    private Long boardId;
    private String content;

    public CommentWriteServiceRequest toService(String email) {
        return CommentWriteServiceRequest.builder()
                .email(email)
                .boardId(boardId)
                .content(content)
                .build();
    }
}
