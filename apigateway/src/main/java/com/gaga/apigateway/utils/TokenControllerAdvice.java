package com.gaga.apigateway.utils;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gaga.apigateway.dto.ErrorMessage;
import com.gaga.apigateway.exception.SignatureVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class TokenControllerAdvice {

    @ExceptionHandler(value = {TokenExpiredException.class})
    public ResponseEntity<ErrorMessage> unauthorizedException(HttpServletRequest req, TokenExpiredException ue) {
        log.error(ue.getMessage(), ue);
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(ue.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }

    @ExceptionHandler(value = {SignatureVerificationException.class})
    public ResponseEntity<ErrorMessage> notFoundException(HttpServletRequest req, SignatureVerificationException sve) {
        log.error(sve.getMessage(), sve);
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(sve.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<ErrorMessage> nullPointerException(HttpServletRequest req, NullPointerException ne) {
        log.error(ne.getMessage(), ne);
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(ne.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }
}
