package com.angelalfaro.kinalapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.angelalfaro.kinalapp.entity.DetailSale;
import com.angelalfaro.kinalapp.service.detailSale.DetailSaleServiceImpl;
import com.angelalfaro.kinalapp.service.product.ProductServiceImpl;
import com.angelalfaro.kinalapp.service.sale.SaleServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/detail-sales")
public class DetailSaleWebController {

    private final DetailSaleServiceImpl detailSaleService;
    private final ProductServiceImpl productService;
    private final SaleServiceImpl saleService;

    @GetMapping
    public String getDetailSales(Model model) {
        return reloadPage(model);
    }

    @PostMapping("/save")
    public String saveDetail(@ModelAttribute("newDetail") DetailSale detailSale, Model model) {
        try {
            detailSaleService.saveDetailSale(detailSale);
            model.addAttribute("successMsg", "El Detalle de venta ha sido registrado exitosamente");

            return reloadPage(model);
        } catch (Exception e){
            model.addAttribute("errorMsg", e.getMessage());
            
            return reloadPage(model);
        }
    }

    @GetMapping("/search")
    public String searchDetail(@RequestParam("codeSearch") Long codeSearch, Model model) {
        try {
            Optional<DetailSale> result = detailSaleService.findByCodeDetailSale(codeSearch);
            model.addAttribute("details", result.isPresent() ? List.of(result.get()) : List.of());
            model.addAttribute("newDetail", new DetailSale());
            model.addAttribute("products", productService.listAllProducts());
            model.addAttribute("sales", saleService.listAllSales());
            return "cruds/detail-sale";
        } catch (Exception e){
            model.addAttribute("errorMsg", e.getMessage());
            
            return reloadPage(model);
        }
    }

    @GetMapping("/edit/{code}")
    public String editDetail(@PathVariable Long code, Model model) {
        try {
            Optional<DetailSale> detailToEdit = detailSaleService.findByCodeDetailSale(code);
        
            if (detailToEdit.isPresent()) {
                model.addAttribute("newDetail", detailToEdit.get());
                model.addAttribute("details", detailSaleService.listAllDetailSale());
                model.addAttribute("products", productService.listAllProducts());
                model.addAttribute("sales", saleService.listAllSales());
                return "cruds/detail-sale";
            }
            return "redirect:/view/detail-sales";
        } catch (Exception e){
            model.addAttribute("errorMsg", e.getMessage());
            
            return reloadPage(model);
        }
    }

    @GetMapping("/delete/{code}")
    public String deleteDetail(@PathVariable Long code, Model model) {
        try {
            detailSaleService.deleteDetailSale(code);
            model.addAttribute("successMsg", "El Detalle de venta ha sido eliminado exitosamente");

            return reloadPage(model);
        } catch (Exception e){
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
        model.addAttribute("details", detailSaleService.listAllDetailSale());
        model.addAttribute("newDetail", new DetailSale());
        model.addAttribute("products", productService.listAllProducts());
        model.addAttribute("sales", saleService.listAllSales());
        return "cruds/detail-sale";
    }
    
}