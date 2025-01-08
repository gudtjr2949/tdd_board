package com.board.board.board.service.dto.request;

import com.board.board.board.repository.entity.Board;
import com.board.board.member.repository.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoardWriteServiceRequest {

    private String title;

    private String content;

    private String email;

    public Board toBoard(Member member) {
        return Board.builder()
                .title(title)
                .content(content)
                .views(0L)
                .member(member)
                .build();
    }
}
