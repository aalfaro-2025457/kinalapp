package com.angelalfaro.kinalapp.controller;

import com.angelalfaro.kinalapp.entity.Client;
import com.angelalfaro.kinalapp.service.client.ClientService;
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
@RequestMapping("/view/clients")
public class ClientWebController {

    private final ClientService clientService;

    @GetMapping
    public String getClients(Model model){

        List<Client> clients = clientService.listAll();
        model.addAttribute("clients", clients);
        model.addAttribute("newClient", new Client());
        return "cruds/clients";
    }

    @PostMapping("/save")
    public String saveClient(@ModelAttribute("newClient") Client client, Model model) {
        try {
            clientService.save(client);
            return "redirect:/view/clients";
        } catch (Exception e){
            List<Client> clients = clientService.listAll();
            model.addAttribute("clients", clients);
            model.addAttribute("newClient", new Client());
            model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
            
            return "cruds/clients";
        }
    }

    @GetMapping("/edit/{dpi}")
    public String editClient(@PathVariable String dpi, Model model) {
        try {
            // Find the Client by dpi
            Optional<Client> clientToEdit = clientService.findByDPI(dpi);

            if (clientToEdit.isPresent()) {
                model.addAttribute("clients", clientService.listAll());

                model.addAttribute("newClient", clientToEdit.get());
                return "cruds/clients";
            }
            return "redirect:/view/clients";
        } catch (Exception e){
            List<Client> clients = clientService.listAll();
            model.addAttribute("clients", clients);
            model.addAttribute("newClient", new Client());
            model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
            
            return "cruds/clients";
        }
    }

    @GetMapping("/delete/{dpi}")
    public String deleteClient(@PathVariable String dpi, Model model) {
        try {
            clientService.delete(dpi);
            return "redirect:/view/clients";
        } catch (Exception e){
            List<Client> clients = clientService.listAll();
            model.addAttribute("clients", clients);
            model.addAttribute("newClient", new Client());
            model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
            
            return "cruds/clients";
        }
    }

    @GetMapping("/search")
    public String searchByDpi(@RequestParam(name = "dpiSearch", required = false) String dpiSearch, Model model) {
        try {
            List<Client> results = new ArrayList<>();

            if (dpiSearch != null && !dpiSearch.trim().isEmpty()) {
                clientService.findByDPI(dpiSearch).ifPresent(results::add);
            } else {
                results = clientService.listAll();
            }

            model.addAttribute("clients", results);
            model.addAttribute("newClient", new Client());
            return "cruds/clients";
        } catch (Exception e){
            List<Client> clients = clientService.listAll();
            model.addAttribute("clients", clients);
            model.addAttribute("newClient", new Client());
            model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
            
            return "cruds/clients";
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        List<Client> clients = clientService.listAll();
        model.addAttribute("clients", clients);
        model.addAttribute("newClient", new Client());
        model.addAttribute("errorMsg", "No tienes el rol de ADMINISTRADOR para realizar esta acción.");
        
        return "cruds/clients";
    }

}
