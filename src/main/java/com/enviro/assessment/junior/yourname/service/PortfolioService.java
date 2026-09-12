package com.enviro.assessment.junior.yourname.service;

import com.enviro.assessment.junior.yourname.dto.InvestorSummaryDTO;
import com.enviro.assessment.junior.yourname.dto.PortfolioDTO;
import com.enviro.assessment.junior.yourname.dto.ProductDTO;
import com.enviro.assessment.junior.yourname.entity.Investor;
import com.enviro.assessment.junior.yourname.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.yourname.repository.InvestorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final InvestorRepository investorRepository;

    public PortfolioService(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    /** Returns every investor as a lightweight summary, for the frontend's investor picker. */
    public List<InvestorSummaryDTO> getAllInvestors() {
        return investorRepository.findAll().stream()
                .map(i -> new InvestorSummaryDTO(i.getId(), i.getFullName()))
                .toList();
    }

    /** Retrieves an investor's full portfolio: their details plus every product they hold. */
    public PortfolioDTO getPortfolio(Long investorId) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + investorId));

        List<ProductDTO> products = investor.getProducts().stream()
                .map(p -> new ProductDTO(p.getId(), p.getProductName(), p.getProductType(), p.getBalance()))
                .toList();

        return new PortfolioDTO(
                investor.getId(),
                investor.getFullName(),
                investor.getEmail(),
                investor.getDateOfBirth(),
                investor.getAge(),
                products
        );
    }
}
