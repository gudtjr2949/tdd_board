package com.board.board.comment.service;

import com.board.board.comment.service.dto.request.CommentDeleteServiceRequest;
import com.board.board.comment.service.dto.request.CommentModifyServiceRequest;
import com.board.board.comment.service.dto.request.CommentWriteServiceRequest;
import com.board.board.comment.service.dto.response.CommentDetailServiceResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    void writeComment(CommentWriteServiceRequest request);
    List<CommentDetailServiceResponse> findCommentsWithBoard(Long boardId);
    void modifyComment(CommentModifyServiceRequest request);
    void deleteComment(CommentDeleteServiceRequest request);
}
