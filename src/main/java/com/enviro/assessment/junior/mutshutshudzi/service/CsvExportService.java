package com.enviro.assessment.junior.mutshutshudzi.service;

import com.enviro.assessment.junior.mutshutshudzi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalStatus;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Turns withdrawal notices into a downloadable CSV statement. Kept
 * separate from WithdrawalService so formatting/export concerns don't
 * mix with business-rule validation.
 */
@Service
public class CsvExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] HEADERS = {
            "Withdrawal ID", "Product ID", "Product Name", "Amount",
            "Balance After", "Type", "Status", "Request Date"
    };

    private final WithdrawalService withdrawalService;

    public CsvExportService(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    public String buildCsv(Long investorId, WithdrawalStatus status, WithdrawalType type,
                            LocalDateTime startDate, LocalDateTime endDate) {
        List<WithdrawalResponseDTO> rows = withdrawalService.getHistory(investorId, status, type, startDate, endDate);

        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", HEADERS)).append("\n");

        for (WithdrawalResponseDTO row : rows) {
            csv.append(row.getId()).append(",")
               .append(row.getProductId()).append(",")
               .append(escape(row.getProductName())).append(",")
               .append(row.getAmount()).append(",")
               .append(row.getBalanceAfter()).append(",")
               .append(row.getWithdrawalType()).append(",")
               .append(row.getStatus()).append(",")
               .append(row.getRequestDate().format(DATE_FORMAT))
               .append("\n");
        }

        return csv.toString();
    }

    /** Quotes any field containing a comma or quote so the CSV stays well-formed. */
    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
