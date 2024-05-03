package com.trabmvc.homebroker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorld {
  
    @GetMapping("/")
    public String index() {
        return "Hello, World!";
    }
}