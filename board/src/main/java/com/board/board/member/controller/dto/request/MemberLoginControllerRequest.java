package com.board.board.member.controller.dto.request;

import com.board.board.member.service.dto.request.MemberLoginServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginControllerRequest {

    private String email;
    private String password;

    @Builder
    private MemberLoginControllerRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public MemberLoginServiceRequest toService() {
        return MemberLoginServiceRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
