
package com.example.p10weeks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello() {
        return "<h1>🚀 Jenkins CI/CD 배포 성공! (v1)</h1>";
    }
}

