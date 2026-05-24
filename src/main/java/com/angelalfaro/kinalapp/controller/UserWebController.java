package com.angelalfaro.kinalapp.controller;

import com.angelalfaro.kinalapp.entity.User;
import com.angelalfaro.kinalapp.service.user.UserServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/user")
public class UserWebController {

    private final UserServiceImpl userService;

    @GetMapping
    public String getUsers(Model model) {
        List<User> users = userService.listAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        return "cruds/user";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User user, Model model) {
        try {
            userService.saveUser(user);
            return "redirect:/view/user";
        } catch (Exception e) {
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("newUser", new User());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/user";
        }
    }

    @GetMapping("/edit/{code}")
    public String editUser(@PathVariable Long code, Model model) {
        try {
            Optional<User> userToEdit = userService.findByCodeUser(code);

            if (userToEdit.isPresent()) {
                model.addAttribute("users", userService.listAllUsers());
                model.addAttribute("newUser", userToEdit.get());
                return "cruds/user";
            }
            return "redirect:/view/user";
        } catch (Exception e) {
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("newUser", new User());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/user";
        }
    }

    @GetMapping("/delete/{code}")
    public String deleteUser(@PathVariable Long code, Model model) {
        try {
            userService.deleteUser(code);
            return "redirect:/view/user";
        } catch (Exception e) {
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("newUser", new User());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/user";
        }
    }

    @GetMapping("/search")
    public String searchByCode(@RequestParam(name = "codeSearch", required = false) Long codeSearch, Model model) {
        try {
            List<User> results = new ArrayList<>();

            if (codeSearch != null) {
                userService.findByCodeUser(codeSearch).ifPresent(results::add);
            } else {
                results = userService.listAllUsers();
            }

            model.addAttribute("users", results);
            model.addAttribute("newUser", new User());
            return "cruds/user";
        } catch (Exception e) {
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("newUser", new User());
            model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
            return "cruds/user";
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("users", userService.listAllUsers());
        model.addAttribute("newUser", new User());
        model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
        return "cruds/user";
    }
}