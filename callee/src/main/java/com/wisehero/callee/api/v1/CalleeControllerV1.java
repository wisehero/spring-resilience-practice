package com.wisehero.callee.api.v1;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.server.SocketSecurityException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.ServiceUnavailableException;

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

	private final AtomicInteger callCounter = new AtomicInteger(0);

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

	/**
	 * Circuit Breaker 전체 상태 전환을 테스트하기 위한 엔드포인트
	 *
	 * 동작 방식:
	 * - 처음 N번(기본 5번)까지는 500 에러 발생
	 * - N+1번째부터는 성공 응답 반환
	 *
	 * 이를 통해 다음 시나리오를 테스트할 수 있습니다:
	 * 1. CLOSED 상태에서 N번 실패 → OPEN으로 전환
	 * 2. OPEN 상태 유지 (설정된 시간만큼)
	 * 3. HALF_OPEN으로 자동 전환
	 * 4. 테스트 호출 성공 → CLOSED로 복구
	 */
	@GetMapping("/circuit-test")
	public ApiResponse<CircuitTestResponse> circuitTest() {
		int currentCount = callCounter.incrementAndGet();

		int failUntilCount = 8;
		log.info("🔵 Circuit Test 호출 - Count: {}, FailUntil: {}",
			currentCount, failUntilCount);

		// 설정된 횟수까지는 실패
		if (currentCount <= failUntilCount) {
			log.error("🔴 의도적 실패 발생 - {}/{}", currentCount, failUntilCount);
			throw new CoreException(
				ErrorType.INTERNAL_ERROR,
				String.format("의도적 실패 (%d/%d)", currentCount, failUntilCount)
			);
		}

		// 설정된 횟수를 넘으면 성공
		log.info("✅ 성공 응답 반환 - Count: {}", currentCount);

		CircuitTestResponse response = new CircuitTestResponse(
			currentCount,
			failUntilCount,
			"Success after " + failUntilCount + " failures",
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
		log.warn("Random 4xx error endpoint called");

		ErrorType[] errorTypes = {
			ErrorType.BAD_REQUEST,
			ErrorType.NOT_FOUND,
			ErrorType.CONFLICT
		};

		ErrorType randomError = errorTypes[new Random().nextInt(errorTypes.length)];
		log.warn("Throwing random error: {}", randomError.getStatus());

		throw new CoreException(randomError, "Random " + randomError.getStatus().getReasonPhrase());
	}

	// 500 오류 응답
	@GetMapping("/500-error")
	public ApiResponse<Object> error5xx() {
		log.error("Error endpoint called");
		throw new CoreException(ErrorType.INTERNAL_ERROR, "Internal error");
	}

	@GetMapping("/503-error")
	public ApiResponse<Object> error503() {
		log.error("503 Service Unavailable endpoint called");
		throw new CoreException(ErrorType.SERVICE_UNAVAILABLE, "Service Unavailable");
	}

	// n초 지연 응답
	@GetMapping("/timeout/{seconds}")
	public ApiResponse<String> timeout(@PathVariable int seconds) throws InterruptedException {
		log.info("Timeout endpoint called with {} seconds", seconds);
		Thread.sleep(seconds * 1000L);
		return ApiResponse.success("Completed after " + seconds + " seconds");
	}

	public record CircuitTestResponse(
		int currentCount,
		int failUnitlCount,
		String message,
		long timestame
	) {
	}

}
