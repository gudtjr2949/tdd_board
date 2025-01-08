package com.board.board.global.service;

import com.board.board.global.exception.CommonErrorCode;
import com.board.board.global.exception.RestApiException;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    // username (email) 이 DB에 존재하는지 확인
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 시큐리티 세션에 유저 정보 저장
        return memberRepository.findMemberByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.DATA_NOT_FOUND));
    }

    // DB 에 Member 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return new org.springframework.security.core.userdetails.User(
                member.getEmail(),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
