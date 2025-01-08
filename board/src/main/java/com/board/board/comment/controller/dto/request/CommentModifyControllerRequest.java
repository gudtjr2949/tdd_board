package com.board.board.comment.controller.dto.request;

import com.board.board.comment.service.dto.request.CommentModifyServiceRequest;
import com.board.board.comment.service.dto.request.CommentWriteServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentModifyControllerRequest {
    private Long commentId;
    private String content;

    public CommentModifyServiceRequest toService(String email) {
        return CommentModifyServiceRequest.builder()
                .email(email)
                .commentId(commentId)
                .content(content)
                .build();
    }
}
