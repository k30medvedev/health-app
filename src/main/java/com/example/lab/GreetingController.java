package com.example.lab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

@RestController
public class GreetingController {

	@GetMapping("/api/greeting")
	public Map<String, String> greeting(@RequestParam(defaultValue = "world") String name) {
		return Map.of("message", "Hello, " + HtmlUtils.htmlEscape(name) + "!");
	}
}
