package com.board.board.board.service;

import com.board.board.board.service.dto.request.BoardDeleteServiceRequest;
import com.board.board.board.service.dto.request.BoardModifyServiceRequest;
import com.board.board.board.service.dto.request.BoardWriteServiceRequest;
import com.board.board.board.service.dto.response.BoardDetailServiceResponse;
import com.board.board.board.service.dto.response.BoardListServiceResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BoardService {
    Long writeBoard(BoardWriteServiceRequest request);
    BoardDetailServiceResponse detailBoard(Long boardId);
    List<BoardListServiceResponse> findAllBoard(Long boardId);
    List<BoardListServiceResponse> findBoardWithRequirements(String conditions, String keyword, Integer page);
    Long modifyBoard(BoardModifyServiceRequest request);
    void deleteBoard(BoardDeleteServiceRequest request);
}
