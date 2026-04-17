package org.example.taskservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.api.dto.ErrorResponse;
import org.example.taskservice.exception.support.SupportException;
import org.example.taskservice.exception.user.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {

        log.info("User error: status={}, msg={}", ex.getHttpStatus(), ex.getUserMessage());

        ErrorResponse body = new ErrorResponse(ex.getHttpStatus().value(), ex.getUserMessage(), null);
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(SupportException.class)
    public ResponseEntity<ErrorResponse> handleSupportException(SupportException ex, HttpServletRequest req) {

        String correlationId = getOrCreateCorrelationId(req);
        log.error("Support error:  cid={}, details={}", correlationId, ex.getDebugDetails(), ex);

        ErrorResponse body = new ErrorResponse(ex.getHttpStatus().value(), "Произошла ошибка. Обратитесь в техподдержку.", correlationId);
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest req) {
        String cid = getOrCreateCorrelationId(req);
        log.error("Unhandled exception, cid={}", cid, ex);

        ErrorResponse body = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Что-то пошло не так", cid);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getOrCreateCorrelationId(HttpServletRequest req) {
        String id = req.getHeader("X-Correlation-Id");
        return id != null ? id : UUID.randomUUID().toString();
    }
}
