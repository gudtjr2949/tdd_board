package com.board.board.board.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoardListServiceRequest {

    private String conditions; // 조건 : 작성자, 제목, 조회수, 댓글수
    private String keyword;
}
