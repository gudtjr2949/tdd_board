package com.board.board.board.repository;

import com.board.board.board.repository.entity.Board;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("게시글을 작성한 후, 게시글의 ID를 사용해 게시글을 조회한다.")
    void writeBoardAndFindBoardUsingBoardId() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Board board = createBoard("게시글 제목", "게시글 내용", member);

        // when
        boardRepository.save(board);
        Optional<Board> findBoard = boardRepository.findById(board.getId());

        // then
        assertThat(findBoard).isPresent();
        assertThat(findBoard.get().getTitle()).isEqualTo(board.getTitle());
        assertThat(findBoard.get().getContent()).isEqualTo(board.getContent());
        assertThat(findBoard.get().getMember().getId()).isEqualTo(board.getMember().getId());
    }

    @Test
    @DisplayName("원하는 조건(전체, 페이징, 제목, 작성자)을 설정한 후, 게시글 리스트를 조회한다.")
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
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").descending());

        // when
        List<Board> all = boardRepository.findAll();
        List<Board> paging = boardRepository.findBoardWithOutCount(pageRequest);
        Page<Board> title = boardRepository.findBoardByTitleContaining("게시글", pageRequest);
        Page<Board> writer = boardRepository.findBoardByMemberNicknameContaining(member.getNickname(), pageRequest);

        // then
        assertThat(all).hasSize(5);
        assertThat(paging).hasSize(3);
        assertThat(title).hasSize(2);
        assertThat(writer).hasSize(3);
    }

    @Test
    @DisplayName("Offset 없이 게시글 리스트를 조회한다.")
    void findBoardListWithoutOffset() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        List<Board> boards = new ArrayList<>();

        for (int i = 1 ; i <= 30 ; i++) {
            boards.add(createBoard("제목 " + i, "내용 " + i, member));
        }
        boardRepository.saveAll(boards);


        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        List<Board> fistPage = boardRepository.findByIdLessThanOrderByIdDesc(boards.get(20).getId(), pageable);
        List<Board> secondPage = boardRepository.findByIdLessThanOrderByIdDesc(boards.get(10).getId(), pageable);


        // then
        assertThat(fistPage).hasSize(10);
        assertThat(secondPage).hasSize(10);
        assertThat(fistPage.get(0).getTitle()).isEqualTo("제목 20");
        assertThat(fistPage.get(9).getTitle()).isEqualTo("제목 11");
        assertThat(secondPage.get(0).getTitle()).isEqualTo("제목 10");
        assertThat(secondPage.get(9).getTitle()).isEqualTo("제목 1");
    }

    @Test
    @DisplayName("Offset 없이 제목이 일치하는 게시글 리스트를 조회한다.")
    void findBoardListUsingTitleWithoutOffset() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        List<Board> boards = new ArrayList<>();

        for (int i = 1 ; i <= 10 ; i++) {
            boards.add(createBoard("Title " + i, "Content " + i, member));
        }

        for (int i = 11 ; i <= 30 ; i++) {
            boards.add(createBoard("제목 " + i, "내용 " + i, member));
        }

        boardRepository.saveAll(boards);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        List<Board> findList = boardRepository.findBoardByTitleContainingAndIdLessThanOrderByIdDesc("제목", 21L, pageable);


        // then
        assertThat(findList).hasSize(10);
        assertThat(findList.get(0).getTitle()).isEqualTo("제목 20");
        assertThat(findList.get(9).getTitle()).isEqualTo("제목 11");
    }

    @Test
    @DisplayName("Offset 없이 작성자가 일치하는 게시글 리스트를 조회한다.")
    void findBoardListUsingWriterWithoutOffset() {
        // given
        Member member1 = createMember("이형석");
        Member member2 = createMember("김철수");

        memberRepository.saveAll(List.of(member1, member2));

        List<Board> boards = new ArrayList<>();

        for (int i = 1 ; i <= 10 ; i++) {
            boards.add(createBoard("제목 " + i, "내용 " + i, member1));
        }

        for (int i = 11 ; i <= 30 ; i++) {
            boards.add(createBoard("제목 " + i, "내용 " + i, member2));
        }

        boardRepository.saveAll(boards);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        List<Board> findList = boardRepository.findBoardByMemberNicknameContainingAndIdLessThanOrderByIdDesc("이형석", boards.get(7).getId(), pageable);


        // then
        assertThat(findList).hasSize(7);
        assertThat(findList.get(0).getTitle()).isEqualTo("제목 7");
        assertThat(findList.get(6).getTitle()).isEqualTo("제목 1");
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void modifiedTitleAndContent() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        String beforeTitle = "게시글 1";
        String beforeContent = "게시글 1";

        Board board = createBoard(beforeTitle, beforeContent, member);
        boardRepository.save(board);

        // when
        board.setTitle("게시글 1 수정");
        board.setContent("게시글 1 수정");
        Optional<Board> byId = boardRepository.findById(board.getId());

        // then
        assertThat(byId).isPresent();
        assertThat(byId.get().getId()).isEqualTo(board.getId());
        assertThat(byId.get().getTitle()).isNotEqualTo(beforeTitle);
        assertThat(byId.get().getContent()).isNotEqualTo(beforeContent);
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
        boardRepository.deleteById(board.getId());
        Optional<Board> byId = boardRepository.findById(board.getId());

        // then
        assertThat(byId).isNotPresent();
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

    private static Member createMember(String nickname) {
        return Member.builder()
                .email("test@example.com")
                .name("테스터")
                .nickname(nickname)
                .phone("010-1234-1234")
                .password("password123")
                .build();
    }

}