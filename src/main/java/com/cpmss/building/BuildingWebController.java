package com.cpmss.building;

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
@RequestMapping("/buildings")
public class BuildingWebController {

    private final BuildingService buildingService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("buildings", buildingService.findAll());
        return "building/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Building building = buildingService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found"));
        model.addAttribute("building", building);
        return "building/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("building", new Building());
        model.addAttribute("isNew", true);
        return "building/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Building building,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "building/form";
        }
        Building saved = buildingService.create(building);
        redirect.addFlashAttribute("success", "Building created successfully.");
        return "redirect:/buildings/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Building building = buildingService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Building not found"));
        model.addAttribute("building", building);
        model.addAttribute("isNew", false);
        return "building/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute Building building,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "building/form";
        }
        buildingService.update(id, building);
        redirect.addFlashAttribute("success", "Building updated successfully.");
        return "redirect:/buildings/" + id;
    }
}
