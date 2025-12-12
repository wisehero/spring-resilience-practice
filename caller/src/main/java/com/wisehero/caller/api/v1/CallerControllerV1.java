package com.wisehero.caller.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wisehero.caller.api.ApiResponse;
import com.wisehero.caller.infra.client.HelloResponse;
import com.wisehero.caller.service.CalleeApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/caller")
@RequiredArgsConstructor
@Slf4j
public class CallerControllerV1 {

	private final CalleeApiService calleeService;

	@GetMapping("/test-hello")
	public ApiResponse<HelloResponse> testHello() {
		log.info("[Caller] test-hello 호출");
		return calleeService.callHello();
	}

	@GetMapping("/test-slow")
	public ApiResponse<String> testSlow() {
		log.info("[Caller] test-slow 호출");
		return calleeService.callSlow();
	}

	@GetMapping("/test-error-4xx")
	public ApiResponse<Object> testError4xx() {
		log.info("[Caller] test-error-4xx 호출");
		return calleeService.call4xxError();
	}

	@GetMapping("/test-error-500")
	public ApiResponse<Object> testError500() {
		log.info("[Caller] test-error-500 호출");
		return calleeService.call500Error();
	}
}
