package com.wisehero.caller.infra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.wisehero.caller.api.ApiResponse;

@FeignClient(
	name = "callee-client-v1",
	url = "${callee.service.url}"
)
public interface CalleeV1Client {
	@GetMapping("/hello")
	ApiResponse<HelloResponse> hello();

	@GetMapping("/slow")
	ApiResponse<String> slow();

	@GetMapping("/4xx-error")
	ApiResponse<Object> error4xx();

	@GetMapping("/500-error")
	ApiResponse<Object> error500();

	@GetMapping("/random-error")
	ApiResponse<String> randomError();

	@GetMapping("/timeout/{seconds}")
	ApiResponse<String> timeout(@PathVariable int seconds);
}
