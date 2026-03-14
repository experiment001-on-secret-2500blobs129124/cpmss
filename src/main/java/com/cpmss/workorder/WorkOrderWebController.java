package com.cpmss.workorder;

import com.cpmss.company.CompanyRepository;
import com.cpmss.person.PersonService;
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
@RequestMapping("/workorders")
public class WorkOrderWebController {

    private final WorkOrderService workOrderService;
    private final PersonService personService;
    private final CompanyRepository companyRepository;

    @GetMapping
    public String list(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isBlank()) {
            model.addAttribute("workOrders", workOrderService.findByStatus(status));
        } else {
            model.addAttribute("workOrders", workOrderService.findAll());
        }
        return "workorder/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        WorkOrder wo = workOrderService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work order not found"));
        model.addAttribute("workOrder", wo);
        model.addAttribute("companies", companyRepository.findAll());
        return "workorder/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("workOrder", new WorkOrder());
        model.addAttribute("staffList", personService.findByType("Staff"));
        model.addAttribute("isNew", true);
        return "workorder/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute WorkOrder workOrder,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("staffList", personService.findByType("Staff"));
            return "workorder/form";
        }
        WorkOrder saved = workOrderService.create(workOrder);
        redirect.addFlashAttribute("success", "Work order created.");
        return "redirect:/workorders/" + saved.getId();
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable UUID id,
                         @RequestParam UUID companyId,
                         RedirectAttributes redirect) {
        workOrderService.assign(id, companyId);
        redirect.addFlashAttribute("success", "Work order assigned to vendor.");
        return "redirect:/workorders/" + id;
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable UUID id, RedirectAttributes redirect) {
        workOrderService.complete(id);
        redirect.addFlashAttribute("success", "Work order completed.");
        return "redirect:/workorders/" + id;
    }
}
