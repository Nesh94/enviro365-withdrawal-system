package com.enviro.assessment.junior.mutshutshudzi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * What the frontend's portfolio dashboard renders: investor details
 * combined with the list of products they hold.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    private Long investorId;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private int age;
    private List<ProductDTO> products;
}
