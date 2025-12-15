package com.wisehero.caller.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.FeignException;
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
				.maxAttempts(3)
				.waitDuration(Duration.ofMillis(500))
				.retryExceptions(
					// 여기에 재시도할 예외를 추가합니다.
					// 추가하지 않을 시 기본적으로 모든 예외에 대해 재시도가 수행됩니다.
				).build());
	}

}
