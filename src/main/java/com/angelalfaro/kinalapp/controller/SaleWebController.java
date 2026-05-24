package com.angelalfaro.kinalapp.controller;

import com.angelalfaro.kinalapp.entity.Sale;
import com.angelalfaro.kinalapp.service.client.ClientService;
import com.angelalfaro.kinalapp.service.sale.SaleServiceImpl;
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
@RequestMapping("/view/sales")
public class SaleWebController {

    private final SaleServiceImpl saleService;
    private final ClientService clientService;
    private final UserServiceImpl userService;

    @GetMapping
    public String getSales(Model model) {
        model.addAttribute("sales", saleService.listAllSales());
        model.addAttribute("newSale", new Sale());
        
        model.addAttribute("clients", clientService.listAll());
        model.addAttribute("users", userService.listAllUsers());
        return "cruds/sale";
    }

    @PostMapping("/save")
    public String saveSale(@ModelAttribute("newSale") Sale sale, Model model) {
        try {
            saleService.saveSale(sale);
            return "redirect:/view/sales";
        } catch (Exception e){
            model.addAttribute("sales", saleService.listAllSales());
            model.addAttribute("newSale", new Sale());
            
            model.addAttribute("clients", clientService.listAll());
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("errorMsg", e.getMessage());
            
            return "cruds/sale";
        }
    }

    @GetMapping("/edit/{code}")
    public String editSale(@PathVariable Long code, Model model) {
        try {
            Optional<Sale> saleToEdit = saleService.findByCodeSale(code);
            if (saleToEdit.isPresent()) {
                model.addAttribute("sales", saleService.listAllSales());
                model.addAttribute("newSale", saleToEdit.get());
                model.addAttribute("clients", clientService.listAll());
                model.addAttribute("users", userService.listAllUsers());
                return "cruds/sale";
            }
            return "redirect:/view/sales";
        } catch (Exception e){
            model.addAttribute("sales", saleService.listAllSales());
            model.addAttribute("newSale", new Sale());
            
            model.addAttribute("clients", clientService.listAll());
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("errorMsg", e.getMessage());
            
            return "cruds/sale";
        }
    }

    @GetMapping("/delete/{code}")
    public String deleteSale(@PathVariable Long code, Model model) {
        try {
            saleService.deleteSale(code);
            return "redirect:/view/sales";
        } catch (Exception e){
            model.addAttribute("sales", saleService.listAllSales());
            model.addAttribute("newSale", new Sale());
            
            model.addAttribute("clients", clientService.listAll());
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("errorMsg", e.getMessage());
            
            return "cruds/sale";
        }
    }

    @GetMapping("/search")
    public String searchByCode(@RequestParam(name = "codeSearch", required = false) Long codeSearch, Model model) {
        try {
            List<Sale> results = new ArrayList<>();
            if (codeSearch != null) {
                saleService.findByCodeSale(codeSearch).ifPresent(results::add);
            } else {
                results = saleService.listAllSales();
            }
            model.addAttribute("sales", results);
            model.addAttribute("newSale", new Sale());
            return "cruds/sale";
        } catch (Exception e){
            model.addAttribute("sales", saleService.listAllSales());
            model.addAttribute("newSale", new Sale());
            
            model.addAttribute("clients", clientService.listAll());
            model.addAttribute("users", userService.listAllUsers());
            model.addAttribute("errorMsg", e.getMessage());
            
            return "cruds/sale";
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("sales", saleService.listAllSales());
        model.addAttribute("newSale", new Sale());
        
        model.addAttribute("clients", clientService.listAll());
        model.addAttribute("users", userService.listAllUsers());
        model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
        
        return "cruds/sale";
    }
}