package com.angelalfaro.kinalapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/home")
public class HomeWebController {

    @GetMapping
    public String goHome() {
        return "home";
    }
    
    @GetMapping("/clients")
    public String goClients() {
        return "redirect:/view/clients";
    }
    
    @GetMapping("/products")
    public String goProducts() {
        return "redirect:/view/products";
    }

    @GetMapping("/sales")
    public String goSales() {
        return "redirect:/view/sales";
    }

    @GetMapping("/detail-sales")
    public String goDetailSales() {
        return "redirect:/view/detail-sales";
    }
    
    @GetMapping("/user")
    public String goUsers() {
        return "redirect:/view/user";
    }

}
