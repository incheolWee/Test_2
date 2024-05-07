package com.example.test_2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sign")
public class SignController {
    @RequestMapping("/")
    public String home(){
        return "/sign/home";
    }
}
