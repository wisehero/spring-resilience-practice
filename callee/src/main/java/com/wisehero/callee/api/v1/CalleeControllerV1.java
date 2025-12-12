package com.wisehero.callee.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wisehero.callee.api.ApiResponse;
import com.wisehero.callee.support.CoreException;
import com.wisehero.callee.support.ErrorType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/callee")
public class CalleeControllerV1 {

	// 단순 호출
	@GetMapping("/hello")
	public ApiResponse<HelloResponse> hello() {
		log.info("Hello Endpoint 호출");
		HelloResponse response = new HelloResponse(
			"Hello From callee",
			System.currentTimeMillis()
		);

		return ApiResponse.success(response);
	}

	// 지연 응답
	@GetMapping("/slow")
	public ApiResponse<String> slow() throws InterruptedException {
		log.info("Slow Endpoint 호출 - 3초 지연");
		Thread.sleep(3000);
		log.info("Slow Endpoint - 지연 완료");

		return ApiResponse.success("Slow reponse completed");
	}

	// 4XX 오류 응답
	@GetMapping("/4xx-error")
	public ApiResponse<Object> error4xx() {
		log.warn("Error endpoint called");

		throw new CoreException(ErrorType.BAD_REQUEST, "BAD REQUEST error");
	}

	// 500 오류 응답
	@GetMapping("/500-error")
	public ApiResponse<Object> error5xx() {
		log.error("Error endpoint called");
		throw new CoreException(ErrorType.INTERNAL_ERROR, "Internal error");
	}

	// n초 지연 응답
	@GetMapping("/timeout/{seconds}")
	public ApiResponse<String> timeout(@PathVariable int seconds) throws InterruptedException {
		log.info("Timeout endpoint called with {} seconds", seconds);
		Thread.sleep(seconds * 1000L);
		return ApiResponse.success("Completed after " + seconds + " seconds");
	}

}
