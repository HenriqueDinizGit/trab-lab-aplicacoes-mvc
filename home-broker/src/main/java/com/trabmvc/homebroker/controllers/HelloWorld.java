package com.trabmvc.homebroker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HelloWorld {
  
    // Requisição simples
    @GetMapping("/")
    public String index (Model model) {
	// Prepara um modelo com os dados a serem utilizados pelo template
        model.addAttribute("message", "Bem vindo ao Lab Spring MVC!");

        // Informa qual template será utilizado (home.html))
        return "home";
    }

    // Passando parâmetros no path da URL
    @GetMapping("/message/{msg}")
    public String message (@PathVariable (value="msg") String msg, Model model) {
        model.addAttribute("message", msg);
        return "home";
    }

}