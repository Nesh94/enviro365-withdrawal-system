package com.enviro.assessment.junior.mutshutshudzi.service;

import com.enviro.assessment.junior.mutshutshudzi.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.mutshutshudzi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mutshutshudzi.entity.Investor;
import com.enviro.assessment.junior.mutshutshudzi.entity.Product;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalType;
import com.enviro.assessment.junior.mutshutshudzi.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.mutshutshudzi.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.mutshutshudzi.repository.ProductRepository;
import com.enviro.assessment.junior.mutshutshudzi.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor retiredInvestor;   // age > 65
    private Investor youngInvestor;     // age <= 65
    private Product product;

    @BeforeEach
    void setUp() {
        retiredInvestor = new Investor(1L, "John", "Smith", "john@example.com",
                LocalDate.now().minusYears(70), null);
        youngInvestor = new Investor(2L, "Sarah", "Johnson", "sarah@example.com",
                LocalDate.now().minusYears(40), null);

        product = new Product();
        product.setId(1L);
        product.setProductName("Retirement Annuity");
        product.setProductType("RETIREMENT_ANNUITY");
        product.setBalance(new BigDecimal("100000.00"));
        product.setInvestor(retiredInvestor);
    }

    @Test
    void retirementWithdrawal_allowedWhenInvestorOverSixtyFive() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(1L, new BigDecimal("1000.00"), WithdrawalType.RETIREMENT);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(withdrawalNoticeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WithdrawalResponseDTO response = withdrawalService.submitWithdrawal(request);

        assertThat(response.getStatus().name()).isEqualTo("APPROVED");
        assertThat(response.getBalanceAfter()).isEqualByComparingTo("99000.00");
    }

    @Test
    void retirementWithdrawal_rejectedWhenInvestorNotOverSixtyFive() {
        product.setInvestor(youngInvestor);
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(1L, new BigDecimal("1000.00"), WithdrawalType.RETIREMENT);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> withdrawalService.submitWithdrawal(request))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("over the age of 65");
    }

    @Test
    void withdrawal_rejectedWhenAmountExceedsBalance() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(1L, new BigDecimal("150000.00"), WithdrawalType.STANDARD);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> withdrawalService.submitWithdrawal(request))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("exceeds available balance");
    }

    @Test
    void withdrawal_rejectedWhenAmountExceedsNinetyPercentOfBalance() {
        // 95% of a 100,000 balance - within balance, but above the 90% cap
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(1L, new BigDecimal("95000.00"), WithdrawalType.STANDARD);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> withdrawalService.submitWithdrawal(request))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("90%");
    }

    @Test
    void withdrawal_allowedAtExactlyNinetyPercentOfBalance() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(1L, new BigDecimal("90000.00"), WithdrawalType.STANDARD);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(withdrawalNoticeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WithdrawalResponseDTO response = withdrawalService.submitWithdrawal(request);

        assertThat(response.getBalanceAfter()).isEqualByComparingTo("10000.00");
    }

    @Test
    void submitWithdrawal_throwsWhenProductDoesNotExist() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(999L, new BigDecimal("100.00"), WithdrawalType.STANDARD);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> withdrawalService.submitWithdrawal(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
