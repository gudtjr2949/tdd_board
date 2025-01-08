package com.board.board.board.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoardModifyServiceRequest {
    private Long boardId;
    private String email;
    private String title;
    private String content;

}
