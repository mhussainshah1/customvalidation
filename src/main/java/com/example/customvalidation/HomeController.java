package com.example.customvalidation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getCustomerPage(Model model) {
        return "customer";
    }

    @PostMapping("/customer")
    public String validateCustomer(@Valid Customer customer, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("message", "The information is invalid!");
        } else {
            model.addAttribute("message", "The information is valid!");
        }
        return "customer";
    }
}
