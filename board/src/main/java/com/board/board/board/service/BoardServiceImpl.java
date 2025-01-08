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
import com.board.board.global.exception.CommonErrorCode;
import com.board.board.global.exception.RestApiException;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.board.board.global.exception.CommonErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final int POST_PER_PAGE = 10;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Long writeBoard(BoardWriteServiceRequest request) {
        Member member = memberRepository.findMemberByEmail(request.getEmail())
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));

        return boardRepository.save(request.toBoard(member)).getId();
    }

    @Override
    @Transactional
    public BoardDetailServiceResponse detailBoard(Long boardId) {
        Board byId = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        byId.increaseViews();

        return BoardDetailServiceResponse.builder()
                .board(byId)
                .comments(commentRepository.findCommentsByBoard(byId))
                .build();
    }

    @Override
    public List<BoardListServiceResponse> findAllBoard(Long boardId) {
        Pageable pageable = PageRequest.of(0, POST_PER_PAGE);
        List<Board> all = boardRepository.findByIdLessThanOrderByIdDesc(boardId, pageable);

        return all.stream()
                .map(board -> BoardListServiceResponse.builder()
                        .board(board)
                        .build())
                .toList();
    }

    // TODO : 리펙토링 필요!
    @Override
    public List<BoardListServiceResponse> findBoardWithRequirements(String conditions, String keyword, Integer page) {
        switch (conditions) {
            case "title" :
                Page<Board> titleBoard = boardRepository.findBoardByTitleContaining(keyword, PageRequest.of(page, POST_PER_PAGE, Sort.by("id").descending()));
                return titleBoard.stream()
                        .map(board -> BoardListServiceResponse.builder()
                                .board(board)
                                .build()).toList();
            case "writer" :
                Page<Board> writerBoard = boardRepository.findBoardByMemberNicknameContaining(keyword, PageRequest.of(page, POST_PER_PAGE, Sort.by("id").descending()));
                return writerBoard.stream()
                        .map(board -> BoardListServiceResponse.builder()
                                .board(board)
                                .build()).toList();
            default:
                List<Board> viewsBoard = boardRepository.findBoardWithOutCount(PageRequest.of(page, POST_PER_PAGE, Sort.by("views").descending()));
                return viewsBoard.stream()
                        .map(board -> BoardListServiceResponse.builder()
                                .board(board)
                                .build()).toList();
        }
    }

    @Override
    @Transactional
    public Long modifyBoard(BoardModifyServiceRequest request) {
        Board byId = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        if (!byId.getMember().getEmail().equals(request.getEmail())) throw new RestApiException(UNAUTHORIZED);

        byId.setTitle(request.getTitle());
        byId.setContent(request.getContent());

        return byId.getId();
    }

    @Override
    public void deleteBoard(BoardDeleteServiceRequest request) {
        Board byId = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new RestApiException(DATA_NOT_FOUND));

        if (!byId.getMember().getEmail().equals(request.getEmail())) throw new RestApiException(UNAUTHORIZED);

        boardRepository.delete(byId);
    }
}
