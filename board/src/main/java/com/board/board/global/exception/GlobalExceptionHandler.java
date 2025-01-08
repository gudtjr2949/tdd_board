package com.board.board.global.exception;

import com.board.board.global.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(RestApiException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(makeResponseBody(e.getErrorCode()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.info("예외 발생 = {}", ex);
        ErrorCode errorCode = makeErrorCode(ex);

        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) ex;
            BindingResult bindingResult = validationException.getBindingResult();
            String detail = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.status(errorCode.getStatus()).body(makeResponseBody(errorCode, detail));
        }

        return ResponseEntity.status(errorCode.getStatus()).body(makeResponseBody(errorCode));
    }

    private ErrorCode makeErrorCode(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return CommonErrorCode.BAD_REQUEST;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return CommonErrorCode.METHOD_NOT_ALLOWED;
        } else if (ex instanceof NoHandlerFoundException) {
            return CommonErrorCode.NOT_FOUND;
        } else if (ex instanceof HttpMessageNotReadableException) {
            return CommonErrorCode.BAD_REQUEST;
        } else if (ex instanceof MethodArgumentNotValidException) {
            return CommonErrorCode.INVALID_INPUT_DATA;
        }
        return CommonErrorCode.INTERNAL_SERVER_ERROR;
    }

    private ExceptionResponse makeResponseBody(ErrorCode errorCode) {
        return new ExceptionResponse(errorCode.getStatus(), errorCode.getStatus().value(), errorCode.getMessage());
    }

    private ExceptionResponse makeResponseBody(ErrorCode errorCode, String detail) {
        return new ExceptionResponse(errorCode.getStatus(), errorCode.getStatus().value(), errorCode.getMessage(), detail);
    }
}