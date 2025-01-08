package com.board.board.member.service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@NoArgsConstructor
public class MemberLoginServiceRequest {
    private String email;
    private String password;

    @Builder
    private MemberLoginServiceRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
