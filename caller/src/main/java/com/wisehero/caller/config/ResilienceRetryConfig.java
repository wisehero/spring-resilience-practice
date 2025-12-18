package com.wisehero.caller.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ResilienceRetryConfig {

	private final RetryRegistry retryRegistry;

	@Bean
	public Retry retry() {
		return retryRegistry.retry("callee-client-v1",
			RetryConfig.custom()
				/*
				 * ========== Resilience4j Retry 주요 옵션 설명 ==========
				 *
				 * 1. maxAttempts(int)
				 *    - 최초 호출을 포함한 최대 시도 횟수
				 *    - 기본값: 3
				 *    - 예: maxAttempts(3) → 최초 1회 + 재시도 2회
				 *
				 * 2. waitDuration(Duration)
				 *    - 재시도 사이의 고정 대기 시간
				 *    - 기본값: 500ms
				 *    - 네트워크 일시적 장애 복구를 위한 시간 확보 목적
				 *
				 * 3. intervalFunction(IntervalFunction)
				 *    - 재시도 간격을 동적으로 조절 (Exponential Backoff 등)
				 *    - waitDuration 대신 사용하면 더 유연한 재시도 전략 가능
				 *    - 예: IntervalFunction.ofExponentialBackoff(500, 2.0)
				 *         → 500ms, 1000ms, 2000ms... 로 대기 시간 증가
				 *    - 예: IntervalFunction.ofExponentialRandomBackoff(500, 2.0, 0.5)
				 *         → Exponential Backoff에 랜덤 지터(jitter) 추가로 thundering herd 방지
				 *
				 * 4. retryExceptions(Class<? extends Throwable>...)
				 *    - 재시도를 수행할 예외 클래스 목록
				 *    - 지정하지 않으면 모든 예외에 대해 재시도 수행
				 *    - 예: retryExceptions(IOException.class, TimeoutException.class)
				 *
				 * 5. ignoreExceptions(Class<? extends Throwable>...)
				 *    - 재시도하지 않고 즉시 실패 처리할 예외 목록
				 *    - 비즈니스 로직 예외나 4xx 클라이언트 오류에 적합
				 *    - 예: ignoreExceptions(IllegalArgumentException.class, FeignException.BadRequest.class)
				 *
				 * 6. retryOnException(Predicate<Throwable>)
				 *    - 예외 조건을 세밀하게 제어하는 Predicate
				 *    - 예: retryOnException(e -> e instanceof FeignException
				 *              && ((FeignException) e).status() >= 500)
				 *         → 5xx 서버 오류만 재시도
				 *
				 * 7. retryOnResult(Predicate<T>)
				 *    - 응답 결과에 따라 재시도 여부 결정
				 *    - 예외가 아닌 특정 응답값에 대해 재시도가 필요할 때 사용
				 *    - 예: retryOnResult(response -> response.getStatusCode() == 204)
				 *
				 * 8. failAfterMaxAttempts(boolean)
				 *    - 최대 시도 후 MaxRetriesExceededException 발생 여부
				 *    - 기본값: false (마지막 예외를 그대로 던짐)
				 *    - true로 설정 시 재시도 실패를 명확히 구분 가능
				 *
				 * ========================================================
				 */
				.maxAttempts(3)
				.waitDuration(Duration.ofMillis(500))
				// .intervalFunction(IntervalFunction.ofExponentialBackoff(500, 2.0))
				.retryExceptions(
					// 여기에 재시도할 예외를 추가합니다.
					// 추가하지 않을 시 기본적으로 모든 예외에 대해 재시도가 수행됩니다.
					// 예: FeignException.FeignServerException.class (5xx 오류)
				)
				// .ignoreExceptions(FeignException.BadRequest.class)
				// .retryOnException(e -> e instanceof FeignException
				//     && ((FeignException) e).status() >= 500)
				// .failAfterMaxAttempts(true)
				.build());
	}

}
