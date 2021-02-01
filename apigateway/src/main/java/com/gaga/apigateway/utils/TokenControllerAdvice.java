package com.gaga.apigateway.utils;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gaga.apigateway.dto.ErrorMessage;
import com.gaga.apigateway.exception.NotTokenException;
import com.gaga.apigateway.exception.SignatureVerificationException;
import com.netflix.zuul.exception.ZuulException;
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
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(ue.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }

    @ExceptionHandler(value = {SignatureVerificationException.class})
    public ResponseEntity<ErrorMessage> signatureVerificationException(HttpServletRequest req, SignatureVerificationException sve) {
        log.error(sve.getMessage(), sve);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(sve.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }

    @ExceptionHandler(value = {JWTDecodeException.class})
    public ResponseEntity<ErrorMessage> JWTDecodeException(HttpServletRequest req, JWTDecodeException de) {
        log.error(de.getMessage(), de);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(de.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }

    @ExceptionHandler(value = {NotTokenException.class})
    public ResponseEntity<ErrorMessage> notTokenException(HttpServletRequest req, NotTokenException nte) {
        log.error(nte.getMessage(), nte);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(nte.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }

    @ExceptionHandler(value = {ZuulException.class})
    public ResponseEntity<ErrorMessage> zuulException(HttpServletRequest req, ZuulException ze) {
        log.error(ze.getMessage(), ze);
        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(ze.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI()));
    }
}
