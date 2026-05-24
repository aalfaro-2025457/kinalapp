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
        return reloadPage(model);
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User user, Model model) {
        try {
            userService.saveUser(user);
            model.addAttribute("successMsg", "El Usuario ha sido registrado exitosamente");

            return reloadPage(model);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return reloadPage(model);
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
            model.addAttribute("errorMsg", e.getMessage());
            return reloadPage(model);
        }
    }

    @GetMapping("/delete/{code}")
    public String deleteUser(@PathVariable Long code, Model model) {
        try {
            userService.deleteUser(code);
            model.addAttribute("successMsg", "El Usuario ha sido eliminado exitosamente");

            return reloadPage(model);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return reloadPage(model);
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
            model.addAttribute("errorMsg", e.getMessage());
            return reloadPage(model);
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
        return reloadPage(model);
    }

    public String reloadPage(Model model){
        List<User> users = userService.listAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        return "cruds/user";
    }
}