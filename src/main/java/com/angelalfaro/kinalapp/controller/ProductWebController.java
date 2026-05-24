package com.angelalfaro.kinalapp.controller;

import com.angelalfaro.kinalapp.entity.Product;
import com.angelalfaro.kinalapp.service.product.ProductServiceImpl;
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
@RequestMapping("/view/products")
public class ProductWebController {

    private final ProductServiceImpl productService;

    @GetMapping
    public String getProducts(Model model) {
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("newProduct", new Product());
        return "cruds/products";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("newProduct") Product product, Model model) {
        try {
            productService.saveProduct(product);
            return "redirect:/view/products";
        } catch (Exception e) {
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("newProduct", new Product());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/products";
        }
    }

    @GetMapping("/edit/{code}")
    public String editProduct(@PathVariable Long code, Model model) {
        try {
            Optional<Product> productToEdit = productService.findByCodeProduct(code);
            if (productToEdit.isPresent()) {
                model.addAttribute("products", productService.listAllProducts());
                model.addAttribute("newProduct", productToEdit.get());
                return "cruds/products";
            }
            return "redirect:/view/products";
        } catch (Exception e) {
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("newProduct", new Product());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/products";
        }
    }

    @GetMapping("/delete/{code}")
    public String deleteProduct(@PathVariable Long code, Model model) {
        try {
            productService.deleteProduct(code);
            return "redirect:/view/products";
        } catch (Exception e) {
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("newProduct", new Product());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/products";
        }
    }

    @GetMapping("/search")
    public String searchByCode(@RequestParam(name = "codeSearch", required = false) Long codeSearch, Model model) {
        try {
            List<Product> results = new ArrayList<>();
            if (codeSearch != null) {
                productService.findByCodeProduct(codeSearch).ifPresent(results::add);
            } else {
                results = productService.listAllProducts();
            }
            model.addAttribute("products", results);
            model.addAttribute("newProduct", new Product());
            return "cruds/products";
        } catch (Exception e) {
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("newProduct", new Product());
            model.addAttribute("errorMsg", e.getMessage());
            return "cruds/products";
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("newProduct", new Product());
        model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
        
        return "cruds/products";
    }
}