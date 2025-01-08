package com.board.board.board.controller.dto.request;

import com.board.board.board.service.dto.request.BoardModifyServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardModifyControllerRequest {
    private Long boardId;
    private String title;
    private String content;

    public BoardModifyServiceRequest toService(String email) {
        return BoardModifyServiceRequest.builder()
                .boardId(boardId)
                .email(email)
                .title(title)
                .content(content)
                .build();
    }
}
