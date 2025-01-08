package com.board.board.comment.controller;

import com.board.board.comment.controller.dto.request.CommentModifyControllerRequest;
import com.board.board.comment.controller.dto.request.CommentWriteControllerRequest;
import com.board.board.comment.service.CommentService;
import com.board.board.comment.service.dto.request.CommentWriteServiceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@ActiveProfiles("test")
@WithMockUser("testUser")
@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CommentService commentService;
    
    @Test
    @DisplayName("댓글을 작성한다.")
    void writeComment() throws Exception {
        // given
        // stubbing 필요 X
        CommentWriteControllerRequest request = CommentWriteControllerRequest.builder()
                .boardId(1L)
                .content("댓글")
                .build();

        // when & then
        mockMvc.perform(
                post("/api/v1/comment/write")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글을 수정한다.")
    void modifyComment() throws Exception {
        // given
        // stubbing 필요 X
        CommentModifyControllerRequest request = CommentModifyControllerRequest.builder()
                .commentId(1L)
                .content("댓글")
                .build();

        // when & then
        mockMvc.perform(
                        post("/api/v1/comment/modify")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    void deleteComment() throws Exception {
        // given
        // stubbing 필요 X

        // when & then
        mockMvc.perform(
                        delete("/api/v1/comment/delete").queryParam("commentId", "1")
                                .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }
}