package com.board.board.comment.repository;

import com.board.board.board.repository.BoardRepository;
import com.board.board.board.repository.entity.Board;
import com.board.board.comment.repository.entity.Comment;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Test
    @DisplayName("댓글을 작성한 후, 조회한다.")
    void writeComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);

        Comment comment = createComment(member, board, "댓글1");

        // when
        commentRepository.save(comment);
        Optional<Comment> byId = commentRepository.findById(comment.getId());


        // then
        assertThat(byId).isPresent();
        assertThat(byId.get().getContent()).isEqualTo(comment.getContent());
    }


    @Test
    @DisplayName("게시글 ID를 사용해 댓글들을 조회한다.")
    void findCommentsUsingBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Board board = createBoard("게시글 제목", "게시글 내용", member);

        // when
        boardRepository.save(board);

        Comment comment1 = createComment(member, board, "댓글1");
        Comment comment2 = createComment(member, board, "댓글2");
        Comment comment3 = createComment(member, board, "댓글3");

        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        List<Comment> commentsByBoard = commentRepository.findCommentsByBoard(board);

        // then
        assertThat(commentsByBoard).hasSize(3)
                .extracting("content").containsExactlyInAnyOrder(
                        "댓글1", "댓글2", "댓글3"
                );
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void modifyComment() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);

        Comment comment = createComment(member, board, "댓글1");
        commentRepository.save(comment);


        // when
        String modifiedContent = "댓글1 수정";
        comment.setContent(modifiedContent);
        Optional<Comment> byId = commentRepository.findById(comment.getId());


        // then
        assertThat(byId).isPresent();
        assertThat(byId.get().getId()).isEqualTo(comment.getId());
        assertThat(byId.get().getContent()).isEqualTo(modifiedContent);
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
        commentRepository.delete(comment);
        Optional<Comment> byId = commentRepository.findById(comment.getId());

        // then
        assertThat(byId).isNotPresent();
    }

    private static Comment createComment(Member member, Board board, String content) {
        return Comment.builder()
                .member(member)
                .board(board)
                .content(content)
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