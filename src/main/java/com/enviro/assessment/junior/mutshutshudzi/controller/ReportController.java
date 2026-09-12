package com.enviro.assessment.junior.mutshutshudzi.controller;

import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalStatus;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalType;
import com.enviro.assessment.junior.mutshutshudzi.service.CsvExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final CsvExportService csvExportService;

    public ReportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    /**
     * Downloads a CSV statement of withdrawal notices. All query
     * parameters are optional filters; omit any of them to widen the export.
     */
    @GetMapping("/withdrawals/csv")
    public ResponseEntity<String> exportWithdrawalsCsv(
            @RequestParam(required = false) Long investorId,
            @RequestParam(required = false) WithdrawalStatus status,
            @RequestParam(required = false) WithdrawalType withdrawalType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String csv = csvExportService.buildCsv(investorId, status, withdrawalType, startDate, endDate);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=withdrawal_statement.csv")
                .body(csv);
    }
}
