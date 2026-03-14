package com.cpmss.workorder;

import com.cpmss.company.CompanyRepository;
import com.cpmss.facility.Facility;
import com.cpmss.facility.FacilityRepository;
import com.cpmss.person.Person;
import com.cpmss.person.PersonRepository;
import com.cpmss.unit.Unit;
import com.cpmss.unit.UnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/workorders")
public class WorkOrderWebController {

    private final WorkOrderService workOrderService;
    private final PersonRepository personRepository;
    private final UnitRepository unitRepository;
    private final FacilityRepository facilityRepository;
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
        populateFormDropdowns(model);
        return "workorder/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute WorkOrder workOrder,
                         @RequestParam(required = false) UUID requestedById,
                         @RequestParam(required = false) UUID targetUnitId,
                         @RequestParam(required = false) UUID targetFacilityId,
                         Model model,
                         RedirectAttributes redirect) {
        try {
            // Resolve FK entities from submitted IDs
            if (requestedById != null) {
                Person person = personRepository.findById(requestedById)
                        .orElseThrow(() -> new EntityNotFoundException("Person not found"));
                workOrder.setRequestedBy(person);
            }
            if (targetUnitId != null) {
                Unit unit = unitRepository.findById(targetUnitId)
                        .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
                workOrder.setTargetUnit(unit);
            }
            if (targetFacilityId != null) {
                Facility facility = facilityRepository.findById(targetFacilityId)
                        .orElseThrow(() -> new EntityNotFoundException("Facility not found"));
                workOrder.setTargetFacility(facility);
            }

            WorkOrder saved = workOrderService.create(workOrder);
            redirect.addFlashAttribute("success", "Work order created.");
            return "redirect:/workorders/" + saved.getId();

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            populateFormDropdowns(model);
            return "workorder/form";
        }
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable UUID id,
                         @RequestParam UUID companyId,
                         RedirectAttributes redirect) {
        try {
            workOrderService.assign(id, companyId);
            redirect.addFlashAttribute("success", "Work order assigned to vendor.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/workorders/" + id;
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable UUID id, RedirectAttributes redirect) {
        try {
            workOrderService.complete(id);
            redirect.addFlashAttribute("success", "Work order completed.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/workorders/" + id;
    }

    private void populateFormDropdowns(Model model) {
        model.addAttribute("persons", personRepository.findAll());
        model.addAttribute("units", unitRepository.findAll());
        model.addAttribute("facilities", facilityRepository.findAll());
    }
}
