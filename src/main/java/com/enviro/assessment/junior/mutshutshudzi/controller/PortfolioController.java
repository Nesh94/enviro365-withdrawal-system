package com.enviro.assessment.junior.mutshutshudzi.controller;

import com.enviro.assessment.junior.mutshutshudzi.dto.InvestorSummaryDTO;
import com.enviro.assessment.junior.mutshutshudzi.dto.PortfolioDTO;
import com.enviro.assessment.junior.mutshutshudzi.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /** Populates the investor picker on the dashboard. */
    @GetMapping("/investors")
    public List<InvestorSummaryDTO> getInvestors() {
        return portfolioService.getAllInvestors();
    }

    /** Returns investor details plus their held products. */
    @GetMapping("/portfolio/{investorId}")
    public PortfolioDTO getPortfolio(@PathVariable Long investorId) {
        return portfolioService.getPortfolio(investorId);
    }
}
