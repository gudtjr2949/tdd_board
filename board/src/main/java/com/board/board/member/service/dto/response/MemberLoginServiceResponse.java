package com.board.board.member.service.dto.response;


import com.board.board.member.repository.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberLoginServiceResponse {
    private String accessToken;
    private String name;
    private String email;
    private String nickname;

    @Builder
    private MemberLoginServiceResponse(String accessToken, Member member) {
        this.accessToken = accessToken;
        this.name = member.getName();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}
