package com.example.todo.controller;

import com.example.todo.dto.UserRegistrationDto;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;  // CHANGED FROM javax.validation to jakarta.validation

@Controller
@RequestMapping("/registration")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto registrationDto,
                                      BindingResult result, Model model) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            return "register";
        }

        // Check if email already exists
        if (userService.findByEmail(registrationDto.getEmail()) != null) {
            result.rejectValue("email", "error.email", "There is already an account registered with this email");
            return "register";
        }

        try {
            userService.save(registrationDto);
            return "redirect:/registration?success";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "register";
        }
    }
}