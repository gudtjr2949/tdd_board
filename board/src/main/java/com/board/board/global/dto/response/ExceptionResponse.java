package com.board.board.global.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ExceptionResponse {
    private HttpStatus status;
    private int code;
    private String message;
    private String detail;

    @Builder
    public ExceptionResponse(HttpStatus status, int code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Builder
    public ExceptionResponse(HttpStatus status, int code, String message, String detail){
        this.status = status;
        this.code = code;
        this.message = message;
        this.detail = detail;
    }
}