package com.board.board.member.service.dto.request;

import com.board.board.member.repository.entity.Authority;
import com.board.board.member.repository.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.board.board.member.repository.entity.Authority.*;

@Getter
public class MemberSignUpServiceRequest {

    private String email;

    private String name;

    private String password;

    private String nickname;

    private String phone;

    @Builder
    private MemberSignUpServiceRequest(String email, String name, String password, String nickname, String phone) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .authority(ROLE_USER)
                .build();
    }
}
