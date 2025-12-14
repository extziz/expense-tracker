package com.project.expense_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HelloController {

    @GetMapping("/")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Expense Tracker API!");
        response.put("version", "1.0");
        response.put("status", "running");
        return response;
    }

    @GetMapping("/api/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("success", true);
        return response;
    }
    @GetMapping("/api/greet/{name}")
    public Map<String, String> greet(@PathVariable String name) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, " + name + "!");
        response.put("greeting", "Welcome to learning Spring Boot!");
        return response;
    }
    @GetMapping("/api/time")
    public Map<String, String> time() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> response = new HashMap<>();
        response.put("Current time is:", now.toString());
        return response;
    }

    @GetMapping("/api/calculate/{num1}/{num2}")
    public Map<String, Integer> calculate(@PathVariable int num1, @PathVariable int num2) {
        Map<String, Integer> response = new HashMap<>();
        response.put(num1 + " + " + num2, num1 + num2);
        return response;
    }

    @GetMapping("/api/search")
    public Map<String, String> search(@RequestParam String keyword) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Searching for " + keyword);
        return response;
    }
}