package com.board.board.board.service;

import com.board.board.board.repository.BoardRepository;
import com.board.board.board.repository.entity.Board;
import com.board.board.board.service.dto.request.BoardDeleteServiceRequest;
import com.board.board.board.service.dto.request.BoardModifyServiceRequest;
import com.board.board.board.service.dto.request.BoardWriteServiceRequest;
import com.board.board.board.service.dto.response.BoardDetailServiceResponse;
import com.board.board.board.service.dto.response.BoardListServiceResponse;
import com.board.board.comment.repository.CommentRepository;
import com.board.board.comment.repository.entity.Comment;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class BoardServiceTest {

    @Autowired
    BoardService boardService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;


    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        boardRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("게시글을 저장하고, 생성된 게시글 ID를 사용해 게시글을 상세조회한다.")
    void writeBoardAndFindBoardUsingBoardId() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        BoardWriteServiceRequest request = createBoardWrite("게시글 제목", "게시글 내용", member.getEmail());

        // when
        Long boardId = boardService.writeBoard(request);

        BoardDetailServiceResponse response = boardService.detailBoard(boardId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getBoardId()).isEqualTo(boardId);
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("게시글을 상세조회할 때, 댓글도 함께 조회된다.")
    void findCommentsWhenFindDetailBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 제목", "게시글 내용", member);
        boardRepository.save(board);

        Comment comment1 = createComment(member, board, "댓글1");
        Comment comment2 = createComment(member, board, "댓글2");
        Comment comment3 = createComment(member, board, "댓글3");
        commentRepository.saveAll(List.of(comment1, comment2, comment3));


        // when
        BoardDetailServiceResponse response = boardService.detailBoard(board.getId());


        // then
        assertThat(response).isNotNull();
        assertThat(response.getBoardId()).isEqualTo(board.getId());
        assertThat(response.getComments()).hasSize(3)
                .extracting("content")
                .containsExactlyInAnyOrder(
                        comment1.getContent(), comment2.getContent(), comment3.getContent()
                );
    }

    @Test
    @Transactional
    @DisplayName("게시글을 상세조회하면 조회수가 1 증가한다.")
    void increaseViewsWhenDetailBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        BoardWriteServiceRequest request = createBoardWrite("게시글 제목", "게시글 내용", member.getEmail());

        // when
        Long boardId = boardService.writeBoard(request);
        BoardDetailServiceResponse response = boardService.detailBoard(boardId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getBoardId()).isEqualTo(boardId);
        assertThat(response.getViews()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글 리스트 작성순으로 조회한다.")
    void findBoardList() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Board board1 = createBoard("게시글 1", "게시글 1", member);
        Board board2 = createBoard("게시글 2", "게시글 2", member);
        Board board3 = createBoard("그냥글 3", "그냥글 3", member);
        Board board4 = createBoard("그냥글 4", "그냥글 4", member);
        Board board5 = createBoard("그냥글 5", "그냥글 5", member);

        boardRepository.saveAll(List.of(board1, board2, board3, board4, board5));

        // when
        List<BoardListServiceResponse> firstPage = boardService.findAllBoard(board5.getId());


        // then
        assertThat(firstPage).hasSize(4);
        assertThat(firstPage.get(0).getTitle()).isEqualTo(board4.getTitle());
        assertThat(firstPage.get(3).getTitle()).isEqualTo(board1.getTitle());
    }

    @Test
    @Transactional
    @DisplayName("원하는 조건(제목, 작성자, 조회순)을 설정한 후, 게시글 리스트를 조회한다.")
    void findBoardListUsingRequirements() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Board board1 = createBoard("게시글 1", "게시글 1", member);
        Board board2 = createBoard("게시글 2", "게시글 2", member);
        Board board3 = createBoard("그냥글 3", "그냥글 3", member);
        Board board4 = createBoard("그냥글 4", "그냥글 4", member);
        Board board5 = createBoard("그냥글 5", "그냥글 5", member);

        boardRepository.saveAll(List.of(board1, board2, board3, board4, board5));

        boardService.detailBoard(board1.getId()); // 첫번째 게시글 조회수 3 증가
        boardService.detailBoard(board1.getId());
        boardService.detailBoard(board1.getId());

        // when
        List<BoardListServiceResponse> titleBoard = boardService.findBoardWithRequirements("title", "게시글", 0);
        List<BoardListServiceResponse> noTitleBoard = boardService.findBoardWithRequirements("title", "없는 제목", 0);

        List<BoardListServiceResponse> writerBoard = boardService.findBoardWithRequirements("writer", member.getNickname(), 0);
        List<BoardListServiceResponse> noWriterBoard = boardService.findBoardWithRequirements("writer", "없는 닉네임", 0);

        List<BoardListServiceResponse> viewsBoard = boardService.findBoardWithRequirements("views", "", 0);

        // then
        assertThat(titleBoard).hasSize(2);
        assertThat(writerBoard).hasSize(3);
        assertThat(viewsBoard.get(0).getBoardId()).isEqualTo(board1.getId());

        assertThat(noTitleBoard).hasSize(0);
        assertThat(noWriterBoard).hasSize(0);
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void modifyBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);

        // when
        BoardModifyServiceRequest request = BoardModifyServiceRequest.builder()
                .boardId(board.getId())
                .title("수정된 게시글 제목 1")
                .content("수정된 게시글 내용 1")
                .email(member.getEmail())
                .build();

        Long modifiedBoardId = boardService.modifyBoard(request);

        BoardDetailServiceResponse response = boardService.detailBoard(modifiedBoardId);

        // then
        assertThat(response.getBoardId()).isEqualTo(request.getBoardId());
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("해당 게시글을 작성하지 않은 사용자가 수정을 시도한다면 예외가 발생한다.")
    void exceptionWhenAnotherWriterTryModifyBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);

        // when
        BoardModifyServiceRequest request = BoardModifyServiceRequest.builder()
                .boardId(board.getId())
                .title("수정된 게시글 제목 1")
                .content("수정된 게시글 내용 1")
                .email("anotherEmail@example.com")
                .build();

        // then
        assertThatThrownBy(() -> boardService.modifyBoard(request)).isInstanceOf(RestApiException.class);
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);

        // when
        BoardDeleteServiceRequest request = BoardDeleteServiceRequest.builder()
                .boardId(board.getId())
                .email(member.getEmail())
                .build();

        boardService.deleteBoard(request);


        // then
        assertThatThrownBy(() -> boardService.detailBoard(request.getBoardId())).isInstanceOf(RestApiException.class);
    }

    @Test
    @DisplayName("해당 게시글을 작성하지 않은 사용자가 삭제를 시도한다면 예외가 발생한다.")
    void exceptionWhenAnotherWriterTryDeleteBoard() {
        // given
        Member member = createMember();
        memberRepository.save(member);
        Board board = createBoard("게시글 1", "게시글 1", member);
        boardRepository.save(board);

        // when
        BoardDeleteServiceRequest request = BoardDeleteServiceRequest.builder()
                .boardId(board.getId())
                .email("anotherEmail@example.com")
                .build();

        // then
        assertThatThrownBy(() -> boardService.deleteBoard(request)).isInstanceOf(RestApiException.class);
    }

    private static Comment createComment(Member member, Board board, String content) {
        return Comment.builder()
                .member(member)
                .board(board)
                .content(content)
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

    private static BoardWriteServiceRequest createBoardWrite(String title, String content, String email) {
        return BoardWriteServiceRequest.builder()
                .title(title)
                .content(content)
                .email(email)
                .build();
    }
}