package com.cpmss.unit;

import com.cpmss.building.BuildingService;
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
@RequestMapping("/units")
public class UnitWebController {

    private final UnitService unitService;
    private final BuildingService buildingService;

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isBlank()) {
            model.addAttribute("units", unitService.findByBuilding(null));
        } else {
            model.addAttribute("units", unitService.findAll());
        }
        model.addAttribute("buildings", buildingService.findAll());
        return "unit/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Unit unit = unitService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
        model.addAttribute("unit", unit);
        return "unit/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("unit", new Unit());
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("isNew", true);
        return "unit/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Unit unit,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("buildings", buildingService.findAll());
            return "unit/form";
        }
        Unit saved = unitService.create(unit);
        redirect.addFlashAttribute("success", "Unit created successfully.");
        return "redirect:/units/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Unit unit = unitService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
        model.addAttribute("unit", unit);
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("isNew", false);
        return "unit/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute Unit unit,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("buildings", buildingService.findAll());
            return "unit/form";
        }
        unitService.update(id, unit);
        redirect.addFlashAttribute("success", "Unit updated successfully.");
        return "redirect:/units/" + id;
    }
}
