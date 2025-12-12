package com.wisehero.caller.service;

import org.springframework.stereotype.Service;

import com.wisehero.caller.api.ApiResponse;
import com.wisehero.caller.infra.client.CalleeV1Client;
import com.wisehero.caller.infra.client.HelloResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalleeApiService {

	private final CalleeV1Client calleeV1Client;

	public ApiResponse<HelloResponse> callHello() {
		log.info("Calling hello endpoint");
		return calleeV1Client.hello();
	}

	@CircuitBreaker(name = "callee-client-v1", fallbackMethod = "slowFallback")
	public ApiResponse<String> callSlow() {
		log.info("Calling slow endpoint");
		return calleeV1Client.slow();
	}

	@CircuitBreaker(name = "callee-client-v1", fallbackMethod = "error4xxFallback")
	public ApiResponse<Object> call4xxError() {
		log.info("Calling error 4xx endpoint");
		return calleeV1Client.error4xx();
	}

	@CircuitBreaker(name = "callee-client-v1", fallbackMethod = "error500Fallback")
	public ApiResponse<Object> call500Error() {
		log.info("Calling error 500 endpoint");
		return calleeV1Client.error500();
	}

	private ApiResponse<HelloResponse> helloFallback(Exception e) {
		log.error("⚠️ Hello Fallback - Reason: {}", e.getMessage());
		HelloResponse fallbackResponse = new HelloResponse(
			"Fallback: Service unavailable",
			System.currentTimeMillis()
		);
		return ApiResponse.success(fallbackResponse);
	}

	private ApiResponse<String> slowFallback(Exception e) {
		log.error("⚠️ Slow Fallback - ErrorName: {} Reason: {}", e.getClass().getSimpleName(), e.getMessage());
		return ApiResponse.success("Fallback: Request timeout");
	}

	private ApiResponse<Object> error4xxFallback(Exception e) {
		log.error("⚠️ 4xx Error Fallback - ErrorName : {} Reason: {}", e.getClass().getSimpleName(), e.getMessage());
		return ApiResponse.success("Fallback: Client error");
	}

	private ApiResponse<Object> error500Fallback(Exception e) {
		log.error("⚠️ 5xx Error Fallback -ErrorName : {} Reason: {}", e.getClass().getSimpleName(), e.getMessage());
		return ApiResponse.success("Fallback: Server error");
	}
}
