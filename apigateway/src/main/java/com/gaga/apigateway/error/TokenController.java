package com.gaga.apigateway.error;

import com.gaga.apigateway.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.web.ZuulController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController extends ZuulController {
	private final JWTUtils jwtUtils;

	@GetMapping("/")
	public String getState() {
		return "API Gateway IS RUNNING";
	}

	@GetMapping("/check/token")
	public ResponseEntity<Void> checkToken(@RequestHeader(value = "token") String token) {
		log.info("token 유효성 파악여부");

		jwtUtils.validateToken(token);

		return ResponseEntity
				.status(HttpStatus.OK)
				.build();
	}
}
