package com.gaga.apigateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gaga.apigateway.exception.NotTokenException;
import com.gaga.apigateway.exception.SignatureVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JWTUtils {

    @Value("${jwt.secret.at}")
    private String KEY;

    public void validateToken(String tokenHeader) {
        log.info("token header : " + tokenHeader);
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(KEY)).build();
            jwtVerifier.verify(tokenHeader);
            log.info("Token validate");
        } catch (TokenExpiredException te) {
            log.error(te.getMessage());
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        } catch (SignatureVerificationException sve) {
            log.error(sve.getMessage());
            throw new SignatureVerificationException("토큰이 변조되었습니다.");
        } catch (JWTDecodeException jde) {
            log.error(jde.getMessage());
            throw new JWTDecodeException("토큰의 유형이 아닙니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NotTokenException("이 토큰이 맞아요?");
        }
    }

    public String decodeTokenToEmail(String token) {
        return JWT.decode(token).getSubject();
    }

    public String decodeTokenToNickName(String token) {
        return JWT.decode(token).getClaim("nickname").toString();
    }
}
