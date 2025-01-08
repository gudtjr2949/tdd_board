package com.board.board.comment.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentDeleteServiceRequest {
    private String email;
    private Long commentId;
}
