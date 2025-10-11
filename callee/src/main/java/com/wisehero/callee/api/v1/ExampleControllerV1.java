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
@RequestMapping("/api")
public class ExampleControllerV1 {

	@GetMapping("/hello")
	public ApiResponse<HelloResponse> hello() {
		log.info("Hello Endpoint 호출");
		HelloResponse response = new HelloResponse(
			"Hello From callee",
			System.currentTimeMillis()
		);

		return ApiResponse.success(response);
	}

	@GetMapping("/slow")
	public ApiResponse<String> slow() throws InterruptedException {
		log.info("Slow Endpoint 호출 - 3초 지연");
		Thread.sleep(3000);
		log.info("Slow Endpoint - 지연 완료");

		return ApiResponse.success("Slow reponse completed");
	}

	@GetMapping("/error")
	public ApiResponse<Object> error() {
		log.error("Error endpoint called");
		throw new CoreException(ErrorType.INTERNAL_ERROR, "Internal error");
	}

	@GetMapping("/timeout/{seconds}")
	public ApiResponse<String> timeout(@PathVariable int seconds) throws InterruptedException {
		log.info("Timeout endpoint called with {} seconds", seconds);
		Thread.sleep(seconds * 1000L);
		return ApiResponse.success("Completed after " + seconds + " seconds");
	}

}
