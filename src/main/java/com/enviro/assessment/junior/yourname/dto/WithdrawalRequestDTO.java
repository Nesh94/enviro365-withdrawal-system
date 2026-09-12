package com.enviro.assessment.junior.yourname.dto;

import com.enviro.assessment.junior.yourname.enums.WithdrawalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payload for POST /api/withdrawals. Field-level annotations catch
 * malformed input (missing fields, non-positive amounts) before the
 * request ever reaches the business-rule validation in the service layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestDTO {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "withdrawalType is required (RETIREMENT or STANDARD)")
    private WithdrawalType withdrawalType;
}
