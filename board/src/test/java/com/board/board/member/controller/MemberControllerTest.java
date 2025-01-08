package com.board.board.member.controller;

import com.board.board.global.exception.RestApiException;
import com.board.board.member.controller.dto.request.MemberLoginControllerRequest;
import com.board.board.member.repository.entity.Member;
import com.board.board.member.service.MemberService;
import com.board.board.member.service.dto.request.MemberLoginServiceRequest;
import com.board.board.member.service.dto.request.MemberSignUpServiceRequest;
import com.board.board.member.service.dto.response.MemberLoginServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.board.board.global.exception.CommonErrorCode.DUPLICATED_DATA;
import static com.board.board.global.exception.CommonErrorCode.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collection;
import java.util.List;


@Slf4j
@ActiveProfiles("test")
@WithMockUser("testUser")
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @MockitoBean
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @TestFactory
    @DisplayName("이메일 중복 조회 시나리오")
    Collection<DynamicTest> checkEmailDuplicated() {
        // given
        when(memberService.checkEmailDuplicated(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);

            if ("test@example.com".equals(email)) {
                throw new RestApiException(DUPLICATED_DATA);
            }
            return true;
        });

        String normalEmail = "test2@example.com";
        String duplicatedEmail = "test@example.com";

        return List.of(
                DynamicTest.dynamicTest("중복되지 않은 이메일이므로, 사용 가능한 이메일이다.", () -> {
                    // when & then
                    mockMvc.perform(
                            get("/api/v1/member/check-email").queryParam("email", normalEmail)
                    ).andDo(print())
                            .andExpect(status().isOk());
                }),
                DynamicTest.dynamicTest("중복된 이메일이므로, 예외가 발생한다.", () -> {
                    // when & then
                    mockMvc.perform(
                                    get("/api/v1/member/check-email").queryParam("email", duplicatedEmail)
                            ).andDo(print())
                            .andExpect(status().isConflict());
                })
        );
    }

    @TestFactory
    @DisplayName("전화번호 중복 여부를 확인한다.")
    Collection<DynamicTest> checkPhoneDuplicated() throws Exception {
        // given
        when(memberService.checkPhoneDuplicated(anyString())).thenAnswer(invocation -> {
           String phone = invocation.getArgument(0);
           return true;
        });

        String phone = "010-1234-1234";


        return List.of(
                DynamicTest.dynamicTest("정상적인 전화번호가 입력되었다.", () -> {
                    // when & then
                    mockMvc.perform(
                                    get("/api/v1/member/check-phone").queryParam("phone", phone)
                            )
                            .andDo(print())
                            .andExpect(status().isOk());
                }),
                DynamicTest.dynamicTest("정상적이지 않은 전화번호 형식이 입력되면 예외가 발생한다.", () -> {
                    // when & then
                    mockMvc.perform(
                                    get("/api/v1/member/check-phone").queryParam("phone", "")
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest());
                })
        );
    }

    @TestFactory
    @DisplayName("닉네임 중복 여부를 확인한다.")
    Collection<DynamicTest> checkNicknameDuplicated() throws Exception {
        // given
        when(memberService.checkNicknameDuplicated(anyString())).thenAnswer(invocation -> {
            String nickname = invocation.getArgument(0);
            return true;
        });

        String nickname = "닉네임";

        return List.of(
                DynamicTest.dynamicTest("정상적인 닉네임이 입력되었다.", () -> {
                    // when & then
                    mockMvc.perform(
                                    get("/api/v1/member/check-nickname").queryParam("nickname", nickname)
                            )
                            .andDo(print())
                            .andExpect(status().isOk());
                }),
                DynamicTest.dynamicTest("정상적이지 않은 닉네임 형식이 입력되면 예외가 발생한다.", () -> {
                    // when & then
                    mockMvc.perform(
                                    get("/api/v1/member/check-nickname").queryParam("nickname", "!@@#")
                            )
                            .andDo(print())
                            .andExpect(status().isBadRequest());
                })
        );
    }

    @TestFactory
    @DisplayName("로그인을 시도한다.")
    Collection<DynamicTest> dynamicTest() {
        // given
        MemberLoginControllerRequest validLoginRequest = MemberLoginControllerRequest.builder()
                .email("test@example.com")
                .password("testPassword123!")
                .build();

        when(memberService.login(any())).thenAnswer(invocation -> {
            MemberLoginServiceRequest request = invocation.getArgument(0);
            if (request.getEmail().equals(validLoginRequest.getEmail()) && request.getPassword().equals(validLoginRequest.getPassword())) {
                return MemberLoginServiceResponse.builder()
                        .accessToken("accessToken")
                        .member(Member.builder()
                                .email(validLoginRequest.getEmail())
                                .name("테스터")
                                .nickname("테스터")
                                .build())
                        .build();
            } else {
                throw new RestApiException(USER_NOT_FOUND);
            }
        });

        MemberLoginControllerRequest inValidLoginRequest = MemberLoginControllerRequest.builder()
                .email("wrongEmail@example.com")
                .password("wrongPassword")
                .build();

        return List.of(
                 DynamicTest.dynamicTest("정상적인 이메일, 비밀번호가 입력되면 정상처리된다.", () -> {
                     // when & then
                     mockMvc.perform(
                             post("/api/v1/member/login")
                                     .content(objectMapper.writeValueAsString(validLoginRequest))
                                     .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                             .andDo(print())
                             .andExpect(status().isOk());
                 }),
                 DynamicTest.dynamicTest("정상적이지 않은 이메일 or 비밀번호가 입력되면 예외가 발생한다.", () -> {
                     // when & then
                     mockMvc.perform(
                             post("/api/v1/member/login")
                                     .content(objectMapper.writeValueAsString(inValidLoginRequest))
                                     .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                             .andDo(print())
                             .andExpect(status().isNotFound());
                 })
         );
    }
}