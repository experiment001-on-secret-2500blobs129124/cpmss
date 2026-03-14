package com.cpmss.contract;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/contracts")
public class ContractWebController {

    private final ContractService contractService;

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isBlank()) {
            model.addAttribute("contracts", contractService.findByStatus(status));
        } else {
            model.addAttribute("contracts", contractService.findAll());
        }
        return "contract/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Contract contract = contractService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        model.addAttribute("contract", contract);
        return "contract/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("contract", new Contract());
        model.addAttribute("isNew", true);
        return "contract/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Contract contract,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "contract/form";
        }
        Contract saved = contractService.create(contract);
        redirect.addFlashAttribute("success", "Contract created as Draft.");
        return "redirect:/contracts/" + saved.getId();
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable UUID id, RedirectAttributes redirect) {
        try {
            contractService.activate(id);
            redirect.addFlashAttribute("success", "Contract activated.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contracts/" + id;
    }

    @PostMapping("/{id}/terminate")
    public String terminate(@PathVariable UUID id, RedirectAttributes redirect) {
        try {
            contractService.terminate(id);
            redirect.addFlashAttribute("success", "Contract terminated.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contracts/" + id;
    }
}
