package com.enviro.assessment.junior.mutshutshudzi.dto;

import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalStatus;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private WithdrawalType withdrawalType;
    private WithdrawalStatus status;
    private LocalDateTime requestDate;
}
