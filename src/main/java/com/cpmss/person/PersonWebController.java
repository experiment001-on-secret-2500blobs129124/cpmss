package com.cpmss.person;

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
@RequestMapping("/persons")
public class PersonWebController {

    private final PersonService personService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("persons", personService.search(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("persons", personService.findAll());
        }
        return "person/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Person person = personService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found"));
        model.addAttribute("person", person);
        return "person/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("isNew", true);
        return "person/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Person person,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "person/form";
        }
        Person saved = personService.create(person);
        redirect.addFlashAttribute("success", "Person created successfully.");
        return "redirect:/persons/" + saved.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Person person = personService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found"));
        model.addAttribute("person", person);
        model.addAttribute("isNew", false);
        return "person/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute Person person,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "person/form";
        }
        personService.update(id, person);
        redirect.addFlashAttribute("success", "Person updated successfully.");
        return "redirect:/persons/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirect) {
        personService.delete(id);
        redirect.addFlashAttribute("success", "Person deleted.");
        return "redirect:/persons";
    }
}
