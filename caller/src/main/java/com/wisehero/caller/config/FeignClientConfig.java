package com.wisehero.caller.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Retryer;

@Configuration
@EnableFeignClients(basePackages = "com.wisehero.caller.infra.client")
public class FeignClientConfig {

	@Bean
	public Retryer retryer() {
		return Retryer.NEVER_RETRY;
	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}
