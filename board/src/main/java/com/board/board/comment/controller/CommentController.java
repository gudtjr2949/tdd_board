package com.board.board.comment.controller;

import com.board.board.comment.controller.dto.request.CommentModifyControllerRequest;
import com.board.board.comment.controller.dto.request.CommentWriteControllerRequest;
import com.board.board.comment.service.CommentService;
import com.board.board.comment.service.dto.request.CommentDeleteServiceRequest;
import com.board.board.global.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/write")
    public ResponseEntity<?> writeComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CommentWriteControllerRequest request) {
        commentService.writeComment(request.toService(userDetails.getUsername()));
        return ResponseEntity.ok().body(MessageResponse.builder().message("댓글 작성 완료").build());
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modifyComment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CommentModifyControllerRequest request) {
        commentService.modifyComment(request.toService(userDetails.getUsername()));
        return ResponseEntity.ok().body(MessageResponse.builder().message("댓글 수정 완료").build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long commentId) {
        commentService.deleteComment(CommentDeleteServiceRequest.builder()
                .email(userDetails.getUsername())
                .commentId(commentId)
                .build());
        return ResponseEntity.ok().body(MessageResponse.builder().message("댓글 삭제 완료").build());
    }
}
