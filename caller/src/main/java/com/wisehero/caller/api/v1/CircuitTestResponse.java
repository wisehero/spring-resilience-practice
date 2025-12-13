package com.wisehero.caller.api.v1;

public record CircuitTestResponse(
	int currentCount,
	int failUntilCount,
	String message,
	long timestamp
) {
}
