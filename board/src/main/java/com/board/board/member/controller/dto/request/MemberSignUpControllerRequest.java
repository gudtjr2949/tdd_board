package com.board.board.member.controller.dto.request;

import com.board.board.member.service.dto.request.MemberSignUpServiceRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignUpControllerRequest {
    private String email;

    private String password;

    private String name;

    private String nickname;

    private String phone;

    public MemberSignUpServiceRequest toService() {
        return MemberSignUpServiceRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .build();
    }
}
