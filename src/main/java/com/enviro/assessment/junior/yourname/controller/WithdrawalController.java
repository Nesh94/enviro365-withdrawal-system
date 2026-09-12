package com.enviro.assessment.junior.yourname.controller;

import com.enviro.assessment.junior.yourname.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.yourname.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.yourname.enums.WithdrawalStatus;
import com.enviro.assessment.junior.yourname.enums.WithdrawalType;
import com.enviro.assessment.junior.yourname.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@CrossOrigin(origins = "*")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    /**
     * Submits a withdrawal notice. @Valid triggers the DTO's field
     * validation; business-rule validation happens inside the service and
     * is reported back via GlobalExceptionHandler as a 400 with a clear
     * message for the UI to display.
     */
    @PostMapping
    public ResponseEntity<WithdrawalResponseDTO> submitWithdrawal(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO response = withdrawalService.submitWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Withdrawal history, with every filter optional. */
    @GetMapping
    public List<WithdrawalResponseDTO> getHistory(
            @RequestParam(required = false) Long investorId,
            @RequestParam(required = false) WithdrawalStatus status,
            @RequestParam(required = false) WithdrawalType withdrawalType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return withdrawalService.getHistory(investorId, status, withdrawalType, startDate, endDate);
    }
}
