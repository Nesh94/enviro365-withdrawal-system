package com.enviro.assessment.junior.mutshutshudzi.service;

import com.enviro.assessment.junior.mutshutshudzi.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.mutshutshudzi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mutshutshudzi.entity.Investor;
import com.enviro.assessment.junior.mutshutshudzi.entity.Product;
import com.enviro.assessment.junior.mutshutshudzi.entity.WithdrawalNotice;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalStatus;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalType;
import com.enviro.assessment.junior.mutshutshudzi.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.mutshutshudzi.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.mutshutshudzi.repository.ProductRepository;
import com.enviro.assessment.junior.mutshutshudzi.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalService {

    /** Retirement withdrawals require the investor to be strictly older than this. */
    static final int MINIMUM_RETIREMENT_AGE = 65;

    /** A withdrawal may never take out more than this fraction of the product balance. */
    static final BigDecimal MAX_WITHDRAWAL_RATIO = new BigDecimal("0.90");

    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public WithdrawalService(ProductRepository productRepository,
                              WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.productRepository = productRepository;
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    /**
     * Validates a withdrawal request against every business rule, and if it
     * passes, deducts the amount from the product balance and records the
     * withdrawal notice. Runs in a single transaction so the balance
     * deduction and the notice record can never end up out of sync.
     *
     * @throws InvalidWithdrawalException if any business rule is violated
     * @throws ResourceNotFoundException  if the product does not exist
     */
    @Transactional
    public WithdrawalResponseDTO submitWithdrawal(WithdrawalRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        Investor investor = product.getInvestor();
        BigDecimal amount = request.getAmount();
        BigDecimal balance = product.getBalance();

        validateBusinessRules(request.getWithdrawalType(), investor, amount, balance);

        BigDecimal balanceAfter = balance.subtract(amount);
        product.setBalance(balanceAfter);
        productRepository.save(product);

        WithdrawalNotice notice = new WithdrawalNotice();
        notice.setProduct(product);
        notice.setInvestor(investor);
        notice.setAmount(amount);
        notice.setBalanceAfter(balanceAfter);
        notice.setWithdrawalType(request.getWithdrawalType());
        notice.setStatus(WithdrawalStatus.APPROVED);
        notice.setRequestDate(LocalDateTime.now());
        withdrawalNoticeRepository.save(notice);

        return toResponseDTO(notice);
    }

    /**
     * Business rules, in the order the assessment lists them:
     * 1. Retirement withdrawals only allowed if age > 65
     * 2. Withdrawal must not exceed balance
     * 3. Withdrawal must not exceed 90% of balance
     */
    private void validateBusinessRules(WithdrawalType type, Investor investor,
                                        BigDecimal amount, BigDecimal balance) {
        if (type == WithdrawalType.RETIREMENT && investor.getAge() <= MINIMUM_RETIREMENT_AGE) {
            throw new InvalidWithdrawalException(
                    "Retirement withdrawals are only allowed for investors over the age of "
                            + MINIMUM_RETIREMENT_AGE + ". Investor is " + investor.getAge() + ".");
        }

        if (amount.compareTo(balance) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (" + format(amount) + ") exceeds available balance (" + format(balance) + ").");
        }

        BigDecimal maxAllowed = balance.multiply(MAX_WITHDRAWAL_RATIO).setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (" + format(amount) + ") exceeds the maximum allowed withdrawal of 90% of balance ("
                            + format(maxAllowed) + ").");
        }
    }

    public List<WithdrawalResponseDTO> getHistory(Long investorId, WithdrawalStatus status,
                                                    WithdrawalType type, LocalDateTime startDate,
                                                    LocalDateTime endDate) {
        return withdrawalNoticeRepository.findWithFilters(investorId, status, type, startDate, endDate)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private WithdrawalResponseDTO toResponseDTO(WithdrawalNotice notice) {
        return new WithdrawalResponseDTO(
                notice.getId(),
                notice.getProduct().getId(),
                notice.getProduct().getProductName(),
                notice.getAmount(),
                notice.getBalanceAfter(),
                notice.getWithdrawalType(),
                notice.getStatus(),
                notice.getRequestDate()
        );
    }

    private String format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
