package com.board.board.board.controller.dto.request;

import com.board.board.board.service.dto.request.BoardWriteServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardWriteControllerRequest {
    private String title;
    private String content;

    public BoardWriteServiceRequest toService(String email) {
        return BoardWriteServiceRequest.builder()
                .email(email)
                .title(title)
                .content(content)
                .build();
    }
}
