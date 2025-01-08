package com.board.board.member.service;

import com.board.board.global.config.jwt.JwtTokenProvider;
import com.board.board.global.exception.CommonErrorCode;
import com.board.board.global.exception.RestApiException;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import com.board.board.member.service.dto.request.MemberLoginServiceRequest;
import com.board.board.member.service.dto.request.MemberSignUpServiceRequest;
import com.board.board.member.service.dto.response.MemberLoginServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.board.board.global.exception.CommonErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(MemberSignUpServiceRequest request) {
        if (memberRepository.findMemberByNameOrEmailOrPhone(request.getName(), request.getEmail(), request.getPhone()).isPresent()) {
            throw new RestApiException(DUPLICATED_DATA);
        }

        memberRepository.save(request.toMember(passwordEncoder));
    }

    @Override
    public boolean checkEmailDuplicated(String email) {
        if (memberRepository.findMemberByEmail(email).isPresent()) throw new RestApiException(DUPLICATED_DATA);
        return true;
    }

    @Override
    public boolean checkNicknameDuplicated(String nickname) {
        if (memberRepository.findMemberByNickname(nickname).isPresent()) throw new RestApiException(DUPLICATED_DATA);
        return true;
    }

    @Override
    public boolean checkPhoneDuplicated(String phone) {
        if (memberRepository.findMemberByPhone(phone).isPresent()) throw new RestApiException(DUPLICATED_DATA);
        return true;
    }

    @Override
    public MemberLoginServiceResponse login(MemberLoginServiceRequest request) {
        Member loginMember = memberRepository.findMemberByEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), loginMember.getPassword())) {
            throw new RestApiException(USER_NOT_FOUND);
        }

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();

        String accessToken = jwtTokenProvider.generateAccessToken(authenticationToken);
        jwtTokenProvider.generateRefreshToken(authenticationToken);

        return MemberLoginServiceResponse.builder()
                .accessToken(accessToken)
                .member(loginMember)
                .build();
    }
}
