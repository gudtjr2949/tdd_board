package com.board.board.member.repository;

import com.board.board.member.repository.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 정보를 입력한 후, 회원 가입을 시도한다.")
    void joinMember() {
        // given
        Member member = createMember();

        // when
        memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(member.getId());

        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getEmail()).isEqualTo(member.getEmail());
        assertThat(findMember.get().getPhone()).isEqualTo(member.getPhone());
    }

    @Test
    @DisplayName("회원가입을 진행하기 전 DB에 존재하는 이메일이거나 전화번호인지 확인한다.")
    void checkBeforeJoinThatExistEmailAndPhone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(member.getEmail());
        Optional<Member> memberByPhone = memberRepository.findMemberByPhone(member.getPhone());

        // then
        assertThat(memberByEmail).isPresent();
        assertThat(memberByPhone).isPresent();
    }

    @Test
    @DisplayName("DB에 존재하는 사용자 정보(이메일, 패스워드)를 입력한다면, 로그인이 성공한다.")
    void successLoginWhenCorrectEmailAndPassword() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> loginMember = memberRepository.findMemberByEmailAndPassword(member.getEmail(), member.getPassword());

        // then
        assertThat(loginMember).isPresent();
        assertThat(loginMember.get().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("DB에 존재하지 않는 사용자 정보(이메일, 패스워드)라면, NULL 이 리턴된다.")
    void nullWhenWrongEmailAndPassword() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> wrongEmailMember = memberRepository.findMemberByEmailAndPassword("wrongEmail@example.com", member.getPassword());
        Optional<Member> wrongPasswordMember = memberRepository.findMemberByEmailAndPassword(member.getEmail(), "wrongPassword123");

        // then
        assertThat(wrongEmailMember).isNotPresent();
        assertThat(wrongPasswordMember).isNotPresent();
    }


    @Test
    @DisplayName("이름과 전화번호를 토대로 회원의 이메일이 무엇인지 확인한다.")
    void findEmailWithNameAndPhone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findMemberByNameAndPhone(member.getName(), member.getPhone());

        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("DB에 존재하지 않는 사용자 정보(이름, 전화번호)라면, NULL 이 리턴된다.")
    void nullWhenWrongNameAndPhone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> wrongNameMember = memberRepository.findMemberByNameAndPhone("잘못된 이름", member.getPhone());
        Optional<Member> wrongPhoneMember = memberRepository.findMemberByNameAndPhone(member.getName(), "111-1111-1111");

        // then
        assertThat(wrongNameMember).isNotPresent();
        assertThat(wrongPhoneMember).isNotPresent();
    }


    @Test
    @DisplayName("이메일과 이름, 전화번호를 토대로 존재하는 회원인지 확인한다.")
    void findMemberWithEmailAndNameAndPhone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findMemberByNameAndEmailAndPhone(member.getName(), member.getEmail(), member.getPhone());

        // then
        assertThat(findMember).isPresent();
        assertThat(findMember.get().getPassword()).isEqualTo(member.getPassword());
    }

    @Test
    @DisplayName("DB에 존재하지 않는 사용자 정보(이름, 이메일, 전화번호)라면, NULL 이 리턴된다.")
    void nullWhenWrongNameAndEmailAndPhone() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> wrongNameMember = memberRepository.findMemberByNameAndEmailAndPhone("wrongName", member.getEmail(), member.getPhone());
        Optional<Member> wrongEmailMember = memberRepository.findMemberByNameAndEmailAndPhone(member.getName(), "wrong@example.com", member.getPhone());
        Optional<Member> wrongPhoneMember = memberRepository.findMemberByNameAndEmailAndPhone(member.getName(), member.getEmail(),"111-1111-1111");

        // then
        assertThat(wrongNameMember).isNotPresent();
        assertThat(wrongEmailMember).isNotPresent();
        assertThat(wrongPhoneMember).isNotPresent();
    }

    private static Member createMember() {
        return Member.builder()
                .email("test@example.com")
                .name("테스터")
                .nickname("테스터")
                .phone("010-1234-1234")
                .password("password123")
                .build();
    }
}