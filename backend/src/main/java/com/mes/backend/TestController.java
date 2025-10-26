package com.mes.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String hello() {
        return "Hello MES!";
    }
    
    @GetMapping("/api/test")
    public String test() {
        return "API Test Success!";
    }
    
}