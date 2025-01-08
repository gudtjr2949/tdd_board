package com.board.board.board.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoardDeleteServiceRequest {
    private Long boardId;
    private String email;
}
