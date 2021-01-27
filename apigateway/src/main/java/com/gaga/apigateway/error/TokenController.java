package com.gaga.apigateway.error;

import com.gaga.apigateway.dto.Message;
import com.gaga.apigateway.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {

	private final JWTUtils jwtUtils;

	@GetMapping("/")
	public String getState() {
		return "API Gateway IS RUNNING";
	}

	@GetMapping("/check/token")
	public ResponseEntity<Message> checkToken(@RequestHeader(value = "token") String token) {

		String message = jwtUtils.validateToken(token);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(new Message(message));
	}

}
