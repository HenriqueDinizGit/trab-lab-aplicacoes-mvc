package com.trabmvc.homebroker.controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trabmvc.homebroker.models.User;
import com.trabmvc.homebroker.repositories.UserRepository;

@Controller
public class LoginController {

    private UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUserAccount(@RequestParam("username") String username,
            @RequestParam("password") String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            return "redirect:/login";
        }

        if (user.get().getPassword() == password) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

}
