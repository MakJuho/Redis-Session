package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/session")
public class SessionController {

	@PostMapping
	public ResponseEntity<Map<String, String>> setAttribute(
			@RequestBody Map<String, String> body,
			HttpSession session) {
		String key = body.get("key");
		String value = body.get("value");
		session.setAttribute(key, value);
		return ResponseEntity.ok(Map.of("key", key, "value", value, "sessionId", session.getId()));
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAttribute(
			@RequestParam String key,
			HttpSession session) {
		Object value = session.getAttribute(key);
		if (value == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(Map.of("key", key, "value", value, "sessionId", session.getId()));
	}

	@DeleteMapping
	public ResponseEntity<Void> invalidateSession(HttpSession session) {
		session.invalidate();
		return ResponseEntity.noContent().build();
	}
}
