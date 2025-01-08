package com.board.board.board.controller;

import com.board.board.board.controller.dto.request.BoardModifyControllerRequest;
import com.board.board.board.controller.dto.request.BoardWriteControllerRequest;
import com.board.board.board.service.BoardService;
import com.board.board.board.service.dto.request.BoardDeleteServiceRequest;
import com.board.board.global.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/write")
    public ResponseEntity<?> writeBoard(@AuthenticationPrincipal UserDetails userDetail, @RequestBody BoardWriteControllerRequest request) {
        return ResponseEntity.ok().body(boardService.writeBoard(request.toService(userDetail.getUsername())));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detailBoard(@RequestParam String boardId) {
        return ResponseEntity.ok().body(boardService.detailBoard(Long.parseLong(boardId)));
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAllBoard(@RequestParam Long boardId) {
        return ResponseEntity.ok().body(boardService.findAllBoard(boardId));
    }

    @GetMapping("/filter-list")
    public ResponseEntity<?> findBoardWithRequirements(@RequestParam String requirement, @RequestParam String keyword, @RequestParam Integer page) {
        return ResponseEntity.ok().body(boardService.findBoardWithRequirements(requirement, keyword, page));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modifyBoard(@AuthenticationPrincipal UserDetails userDetail, @RequestBody BoardModifyControllerRequest request) {
        return ResponseEntity.ok().body(boardService.modifyBoard(request.toService(userDetail.getUsername())));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal UserDetails userDetail, @RequestParam Long boardId) {
        boardService.deleteBoard(BoardDeleteServiceRequest.builder()
                .boardId(boardId)
                .email(userDetail.getUsername())
                .build());

        return ResponseEntity.ok().body(MessageResponse.builder()
                .message("삭제 완료")
                .build());
    }
}
