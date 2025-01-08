package com.board.board.member.service;

import com.board.board.member.service.dto.request.MemberLoginServiceRequest;
import com.board.board.member.service.dto.request.MemberSignUpServiceRequest;
import com.board.board.member.service.dto.response.MemberLoginServiceResponse;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void signUp(MemberSignUpServiceRequest request);

    boolean checkEmailDuplicated(String email);

    boolean checkNicknameDuplicated(String nickname);

    boolean checkPhoneDuplicated(String phone);

    MemberLoginServiceResponse login(MemberLoginServiceRequest request);
}
