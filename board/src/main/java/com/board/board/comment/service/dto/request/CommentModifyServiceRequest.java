package com.board.board.comment.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentModifyServiceRequest {
    private Long commentId;
    private String email;
    private String content;
}
