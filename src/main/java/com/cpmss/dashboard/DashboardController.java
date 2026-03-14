package com.cpmss.dashboard;

import com.cpmss.contract.ContractService;
import com.cpmss.installment.InstallmentRepository;
import com.cpmss.payment.PaymentService;
import com.cpmss.person.PersonService;
import com.cpmss.unit.UnitService;
import com.cpmss.workorder.WorkOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class DashboardController {

    private final PersonService personService;
    private final UnitService unitService;
    private final ContractService contractService;
    private final PaymentService paymentService;
    private final WorkOrderService workOrderService;
    private final InstallmentRepository installmentRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        // Stats
        model.addAttribute("totalPersons", personService.count());
        model.addAttribute("totalUnits", unitService.count());
        model.addAttribute("occupiedUnits", unitService.countByStatus("Occupied"));
        model.addAttribute("vacantUnits", unitService.countByStatus("Vacant"));
        model.addAttribute("activeContracts", contractService.countByStatus("Active"));
        model.addAttribute("totalRevenue", paymentService.totalInbound());
        model.addAttribute("openWorkOrders", workOrderService.countByStatus("Pending")
                + workOrderService.countByStatus("Assigned")
                + workOrderService.countByStatus("In Progress"));
        model.addAttribute("overdueInstallments", installmentRepository.countByStatus("Overdue"));

        return "dashboard/index";
    }
}
