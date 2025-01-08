package com.board.board.board.controller;

import com.board.board.board.controller.dto.request.BoardModifyControllerRequest;
import com.board.board.board.controller.dto.request.BoardWriteControllerRequest;
import com.board.board.board.service.BoardService;

import com.board.board.board.service.dto.response.BoardDetailServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WithMockUser("testUser")
@WebMvcTest(controllers = BoardController.class)
class BoardControllerTest {

    @MockitoBean
    BoardService boardService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("게시글을 작성한다.")
    void writeBoard() throws Exception {
        // given
        when(boardService.writeBoard(any())).thenReturn(1L);

        BoardWriteControllerRequest request = BoardWriteControllerRequest.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .build();

        // when & then
        mockMvc.perform(
                post("/api/v1/board/write")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 게시글을 상세 조회한다.")
    void detailBoard() throws Exception {
        // given
        when(boardService.detailBoard(anyLong())).thenReturn(null);

        // when & then
        mockMvc.perform(
                        get("/api/v1/board/detail").queryParam("boardId", "1")
        ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 리스트 작성순으로 조회한다.")
    void findBoardList() throws Exception {
        // given
        when(boardService.findAllBoard(anyLong())).thenReturn(null);

        // when & then
        mockMvc.perform(
                        get("/api/v1/board/list").queryParam("boardId", "1")
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("원하는 조건(제목, 작성자, 조회순)을 설정한 후, 게시글 리스트를 조회한다.")
    void findBoardListUsingRequirements() throws Exception {
        // given
        when(boardService.findBoardWithRequirements(anyString(), anyString(), anyInt())).thenReturn(null);

        // when & then
        mockMvc.perform(
                get("/api/v1/board/filter-list")
                        .queryParam("requirement", "title")
                        .queryParam("keyword", "게시글")
                        .queryParam("page", "0"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void modifyBoard() throws Exception {
        // given
        when(boardService.modifyBoard(any())).thenReturn(null);

        BoardModifyControllerRequest request = BoardModifyControllerRequest.builder()
                .boardId(1L)
                .title("수정할 제목")
                .content("수정할 내용")
                .build();

        // when & then
        mockMvc.perform(
                post("/api/v1/board/modify")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteBoard() throws Exception {
        // given
        // stubbing 필요 X

        // when & then
        mockMvc.perform(
                delete("/api/v1/board/delete").queryParam("boardId", "1")
                        .with(csrf())
        ).andDo(print())
                .andExpect(status().isOk());
    }
}