package com.board.board.comment.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentDetailServiceResponse {
    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
}
