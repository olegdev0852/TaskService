package org.example.taskservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.taskservice.dto.ErrorResponse;
import org.example.taskservice.exception.support.SupportException;
import org.example.taskservice.exception.user.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {

        log.info("User error: code={}, msg={}", ex.getErrorCode(), ex.getUserMessage());

        ErrorResponse body = new ErrorResponse(ex.getErrorCode(), ex.getUserMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(SupportException.class)
    public ResponseEntity<ErrorResponse> handleSupportException(SupportException ex, HttpServletRequest req) {

        String correlationId = getOrCreateCorrelationId(req);
        log.error("Support error:  cid={}, details={}", correlationId, ex.getDebugDetails(), ex);

        ErrorResponse body = new ErrorResponse("INTERNAL_ERROR", "Произошла ошибка. Обратитесь в техподдержку.", correlationId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest req) {
        String cid = getOrCreateCorrelationId(req);
        log.error("Unhandled exception, cid={}", cid, ex);
        ErrorResponse body = new ErrorResponse("UNEXPECTED", "Что-то пошло не так", cid);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getOrCreateCorrelationId(HttpServletRequest req) {
        String id = req.getHeader("X-Correlation-Id");
        return id != null ? id : UUID.randomUUID().toString();
    }
}
