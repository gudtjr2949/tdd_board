package com.board.board.comment.service;

import com.board.board.board.repository.BoardRepository;
import com.board.board.board.repository.entity.Board;
import com.board.board.comment.repository.CommentRepository;
import com.board.board.comment.repository.entity.Comment;
import com.board.board.comment.service.dto.request.CommentDeleteServiceRequest;
import com.board.board.comment.service.dto.request.CommentModifyServiceRequest;
import com.board.board.comment.service.dto.request.CommentWriteServiceRequest;
import com.board.board.comment.service.dto.response.CommentDetailServiceResponse;
import com.board.board.global.exception.RestApiException;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.board.board.global.exception.CommonErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;


    @Override
    public void writeComment(CommentWriteServiceRequest request) {
        Member member = memberRepository.findMemberByEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));

        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        commentRepository.save(request.toComment(member, board));
    }

    @Override
    public List<CommentDetailServiceResponse> findCommentsWithBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        List<Comment> comments = commentRepository.findCommentsByBoard(board);

        return comments.stream().map(
                comment -> CommentDetailServiceResponse.builder()
                        .commentId(comment.getId())
                        .nickname(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .build()
        ).toList();
    }

    @Override
    @Transactional
    public void modifyComment(CommentModifyServiceRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        // 해당 댓글을 작성한 사용자가 맞는지 확인
        if (!comment.getMember().getEmail().equals(request.getEmail())) throw new RestApiException(UNAUTHORIZED);

        comment.setContent(request.getContent());
    }

    @Override
    public void deleteComment(CommentDeleteServiceRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        // 해당 댓글을 작성한 사용자가 맞는지 확인
        if (!comment.getMember().getEmail().equals(request.getEmail())) throw new RestApiException(UNAUTHORIZED);

        commentRepository.delete(comment);
    }
}
