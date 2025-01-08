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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;


    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("게시글에 댓글을 작성한 후, 게시글 ID를 사용해 댓글을 조회한다.")
    void writeCommentAndFindWithBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);


        // when
        CommentWriteServiceRequest request = createCommentRequest(member, board);
        commentService.writeComment(request);
        List<CommentDetailServiceResponse> commentsWithBoard = commentService.findCommentsWithBoard(board.getId());


        // then
        assertThat(commentsWithBoard).hasSize(1)
                .extracting("content").containsExactlyInAnyOrder(
                        "댓글 내용"
                );
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void modifyComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);
        Comment comment = createComment(member, board, "댓글1");
        commentRepository.save(comment);


        // when
        String modifiedContent = "수정내용";
        CommentModifyServiceRequest modifyRequest = createModifyRequest(member.getEmail(), comment.getId(), modifiedContent);
        commentService.modifyComment(modifyRequest);
        Optional<Comment> byId = commentRepository.findById(comment.getId());

        // then
        assertThat(byId).isPresent();
        assertThat(byId.get().getId()).isEqualTo(comment.getId());
        assertThat(byId.get().getContent()).isEqualTo(modifiedContent);
    }

    @Test
    @DisplayName("해당 댓글을 작성하지 않은 사용자가 수정을 시도하면 예외가 발생한다.")
    void exceptionWhenAnotherWriterTryModifyComment() {
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);
        Comment comment = createComment(member, board, "댓글1");
        commentRepository.save(comment);

        String modifiedContent = "수정내용";
        CommentModifyServiceRequest modifyRequest = createModifyRequest("anotherEmail@example.com", comment.getId(), modifiedContent);

        // when & then
        assertThatThrownBy(() -> commentService.modifyComment(modifyRequest)).isInstanceOf(RestApiException.class);
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);
        Comment comment = createComment(member, board, "댓글1");
        commentRepository.save(comment);


        // when
        CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.builder()
                .commentId(comment.getId())
                .email(member.getEmail())
                .build();

        commentService.deleteComment(deleteRequest);
        List<CommentDetailServiceResponse> commentsWithBoard = commentService.findCommentsWithBoard(board.getId());


        // then
        assertThat(commentsWithBoard).hasSize(0);
    }

    @Test
    @DisplayName("해당 댓글을 작성하지 않은 사용자가 삭제를 시도하면 예외가 발생한다.")
    void exceptionWhenAnotherWriterTryDeleteComment() {
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);
        Comment comment = createComment(member, board, "댓글1");
        commentRepository.save(comment);

        CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.builder()
                .commentId(comment.getId())
                .email("anotherEmail@example.com")
                .build();


        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(deleteRequest)).isInstanceOf(RestApiException.class);
    }

    private static CommentModifyServiceRequest createModifyRequest(String email, Long commentId, String content) {
        return CommentModifyServiceRequest.builder()
                .commentId(commentId)
                .email(email)
                .content(content)
                .build();
    }

    private static Comment createComment(Member member, Board board, String content) {
        return Comment.builder()
                .member(member)
                .board(board)
                .content(content)
                .build();
    }


    private static CommentWriteServiceRequest createCommentRequest(Member member, Board board) {
        return CommentWriteServiceRequest.builder()
                .boardId(board.getId())
                .email(member.getEmail())
                .content("댓글 내용")
                .build();
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

    private static Board createBoard(String title, String content, Member member) {
        return Board.builder()
                .title(title)
                .content(content)
                .member(member)
                .views(0L)
                .build();
    }
}