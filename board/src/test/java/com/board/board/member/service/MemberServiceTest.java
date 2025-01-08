package com.board.board.member.service;

import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import com.board.board.member.service.dto.request.MemberLoginServiceRequest;
import com.board.board.member.service.dto.request.MemberSignUpServiceRequest;
import com.board.board.member.service.dto.response.MemberLoginServiceResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;


    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입 전 이메일, 닉네임, 전화번호의 중복여부를 확인하고 문제가 없는 경우, 정상처리한다.")
    void beforeSignUpCheckEmailAndNicknameAndPhone() {
        // given
        String email = "test1@example.com";
        String nickname = "테스터1";
        String phone = "010-1111-1111";


        // when
        boolean checkEmail = memberService.checkEmailDuplicated(email);
        boolean checkNickname = memberService.checkNicknameDuplicated(nickname);
        boolean checkPhone = memberService.checkPhoneDuplicated(phone);


        // then
        assertThat(checkEmail).isTrue();
        assertThat(checkNickname).isTrue();
        assertThat(checkPhone).isTrue();
    }

    @Test
    @DisplayName("회원가입 전 이메일, 닉네임, 전화번호의 중복여부를 확인하고 문제가 있는 경우 예외가 발생한다.")
    void exceptionBeforeSignUpCheckEmailAndNicknameAndPhone() {
        // given
        MemberSignUpServiceRequest member = createMember();
        memberService.signUp(member);

        // when & then
        assertThatThrownBy(() -> memberService.checkEmailDuplicated(member.getEmail())).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.checkNicknameDuplicated(member.getNickname())).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.checkPhoneDuplicated(member.getPhone())).isInstanceOf(RuntimeException.class);
    }



    @Test
    @DisplayName("정보를 입력하고, 회원가입을 시도한다.")
    void signUpMember() {
        // given
        MemberSignUpServiceRequest request = createMember();
        memberService.signUp(request);

        // when
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(request.getEmail());

        // then
        assertThat(memberByEmail).isPresent();
        assertThat(memberByEmail.get().getEmail()).isEqualTo(request.getEmail());
        assertThat(memberByEmail.get().getPhone()).isEqualTo(request.getPhone());
        assertThat(memberByEmail.get().getName()).isEqualTo(request.getName());
        assertThat(memberByEmail.get().getNickname()).isEqualTo(request.getNickname());
    }

    @Test
    @DisplayName("최종으로 회원가입을 끝낼 때, 이메일, 닉네임, 전화번호중 하나라도 중복된 경우, 예외가 발생한다.")
    void exceptionWhenDuplicatedEmailOrNicknameOrPhone() {
        // given
        MemberSignUpServiceRequest request = createMember();
        memberService.signUp(request);

        MemberSignUpServiceRequest duplicatedNickname = createMember(request.getNickname());
        MemberSignUpServiceRequest duplicatedEmail = createMemberWithEmail(request.getEmail());
        MemberSignUpServiceRequest duplicatedPhone = createMemberWithPhone(request.getPhone());

        // when & then
        assertThatThrownBy(() -> memberService.signUp(duplicatedNickname)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.signUp(duplicatedEmail)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.signUp(duplicatedPhone)).isInstanceOf(RuntimeException.class);
    }

    // TODO
    @Test
    @DisplayName("회원가입할 때, DB에 저장하기 전, 비밀번호를 암호화하고 저장한다.")
    void beforeSignUpEncryptPassword() {
        // given


        // when


        // then
    }

    @Test
    @DisplayName("DB에 존재하는 이메일과 비밀번호를 입력하고 로그인을 시도하면 성공한다.")
    void loginWithCorrectEmailAndPassword() {
        // given
        MemberSignUpServiceRequest request = createMember();
        memberService.signUp(request);

        MemberLoginServiceRequest loginRequest = MemberLoginServiceRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        // when
        MemberLoginServiceResponse loginMember = memberService.login(loginRequest);

        // then
        assertThat(loginMember).isNotNull();
        assertThat(loginMember.getEmail()).isEqualTo(request.getEmail());
        assertThat(loginMember.getName()).isEqualTo(request.getName());
        assertThat(loginMember.getNickname()).isEqualTo(request.getNickname());
    }

    @Test
    @DisplayName("DB에 존재하지 않는 이메일이나 비밀번호를 입력하고 로그인을 시도하면 예외가 발생한다.")
    void exceptionWhenLoginWithWrongEmailOrPassword() {
        // given
        MemberSignUpServiceRequest request = createMember();
        memberService.signUp(request);

        MemberLoginServiceRequest wrongEmailLoginRequest = MemberLoginServiceRequest.builder()
                .email("wrong@example.com")
                .password(request.getPassword())
                .build();

        MemberLoginServiceRequest wrongPasswordLoginRequest = MemberLoginServiceRequest.builder()
                .email(request.getEmail())
                .password("wrongPassword123")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.login(wrongEmailLoginRequest)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.login(wrongPasswordLoginRequest)).isInstanceOf(RuntimeException.class);
    }

    private static MemberSignUpServiceRequest createMember() {
        return  MemberSignUpServiceRequest.builder()
                .email("test@example.com")
                .name("테스터")
                .nickname("테스터")
                .phone("010-1234-1234")
                .password("password123")
                .build();
    }

    private static MemberSignUpServiceRequest createMember(String nickname) {
        return  MemberSignUpServiceRequest.builder()
                .email("test@example.com")
                .name("테스터")
                .nickname(nickname)
                .phone("010-1234-1234")
                .password("password123")
                .build();
    }

    private static MemberSignUpServiceRequest createMemberWithPhone(String phone) {
        return  MemberSignUpServiceRequest.builder()
                .email("test@example.com")
                .name("테스터")
                .nickname("테스터")
                .phone(phone)
                .password("password123")
                .build();
    }

    private static MemberSignUpServiceRequest createMemberWithEmail(String email) {
        return  MemberSignUpServiceRequest.builder()
                .email(email)
                .name("테스터")
                .nickname("테스터")
                .phone("010-1234-1234")
                .password("password123")
                .build();
    }
}