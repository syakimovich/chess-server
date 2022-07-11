package com.github.syakimovich.chessserver.controllers;

import com.github.syakimovich.chessserver.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String getSignup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@RequestParam("username") String username, @RequestParam("password") String password) {
        userService.createUser(username, password);
        return "redirect:/login";
    }
}
