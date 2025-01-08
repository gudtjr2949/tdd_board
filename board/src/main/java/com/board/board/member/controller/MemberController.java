package com.board.board.member.controller;

import com.board.board.global.exception.CommonErrorCode;
import com.board.board.global.exception.RestApiException;
import com.board.board.member.controller.dto.request.MemberLoginControllerRequest;
import com.board.board.member.controller.dto.request.MemberSignUpControllerRequest;
import com.board.board.member.service.MemberService;
import com.board.board.member.service.dto.request.MemberSignUpServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

import static com.board.board.global.exception.CommonErrorCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailDuplicated(@RequestParam String email) {
        memberService.checkEmailDuplicated(email);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhoneDuplicated(@RequestParam String phone) {
        // 전화번호 형식 검증
        if (!isValidPhoneNumber(phone)) {
            throw new RestApiException(INVALID_INPUT_DATA);
        }

        memberService.checkPhoneDuplicated(phone);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNicknameDuplicated(@RequestParam String nickname) {
        // 닉네임 형식 검증
        if (!insValidNickname(nickname)) {
            throw new RestApiException(INVALID_INPUT_DATA);
        }

        memberService.checkNicknameDuplicated(nickname);
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody MemberSignUpControllerRequest request) {
        memberService.signUp(request.toService());
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginControllerRequest request) {
        return ResponseEntity.ok().body(memberService.login(request.toService()));
    }

    private boolean insValidNickname(String nickname) {
        String nicknamePattern = "^[a-zA-Z0-9가-힣]*$";
        return Pattern.matches(nicknamePattern, nickname);
    }

    private boolean isValidPhoneNumber(String phone) {
        // 전화번호 패턴: "XXX-XXXX-XXXX"
        String phonePattern = "^\\d{3}-\\d{4}-\\d{4}$";
        return Pattern.matches(phonePattern, phone);
    }
}
