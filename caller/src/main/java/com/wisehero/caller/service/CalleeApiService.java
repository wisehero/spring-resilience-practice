package com.wisehero.caller.service;

import org.springframework.stereotype.Service;

import com.wisehero.caller.api.ApiResponse;
import com.wisehero.caller.api.v1.CallerControllerV1;
import com.wisehero.caller.api.v1.CircuitTestResponse;
import com.wisehero.caller.infra.client.CalleeV1Client;
import com.wisehero.caller.infra.client.HelloResponse;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalleeApiService {

	private final CalleeV1Client calleeV1Client;

	@CircuitBreaker(name = "callee-client-v1", fallbackMethod = "circuitTestFallback")
	public ApiResponse<HelloResponse> callHello() {
		log.info("Calling hello endpoint");
		return calleeV1Client.hello();
	}

	@CircuitBreaker(name = "callee-client-v1", fallbackMethod = "circuitTestFallback")
	public ApiResponse<CircuitTestResponse> callCircuitTest() {
		log.info("Calling circuit-test endpoint");
		return calleeV1Client.circuitTest();
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
	@Retry(name = "callee-client-v1")
	public ApiResponse<Object> call500Error() {
		log.info("Calling error 500 endpoint");
		return calleeV1Client.error500();
	}

	@CircuitBreaker(name = "callee-client-v1")
	@Retry(name = "callee-client-v1")
	public ApiResponse<Object> call503Error() {
		log.info("Calling error 503 endpoint");
		return calleeV1Client.error503();
	}

	private ApiResponse<HelloResponse> helloFallback(Exception e) {
		log.error("⚠️ Hello Fallback - Reason: {}", e.getMessage());
		HelloResponse fallbackResponse = new HelloResponse(
			"Fallback: Service unavailable",
			System.currentTimeMillis()
		);
		return ApiResponse.success(fallbackResponse);
	}

	private ApiResponse<CircuitTestResponse> circuitTestFallback(Exception e) {
		log.error("⚠️ Circuit Test Fallback - Reason: {}", e.getMessage());

		// Circuit이 OPEN 상태인지 확인
		if (e instanceof CallNotPermittedException) {
			log.error("🔴 Circuit is OPEN - Call not permitted!");
		}

		CircuitTestResponse fallbackResponse = new CircuitTestResponse(
			-1,
			-1,
			"Fallback: " + e.getClass().getSimpleName(),
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

	// Fallback 메서드
	private ApiResponse<Object> error503Fallback(Exception e) {
		log.error("⚠️ 503 Error Fallback - Full Exception Class: {}",
			e.getClass().getName());  // 전체 클래스 이름 출력
		log.error("⚠️ 503 Error Fallback - ErrorName: {} Reason: {}",
			e.getClass().getSimpleName(), e.getMessage());
		return ApiResponse.success("Fallback: Service Unavailable");
	}
}
