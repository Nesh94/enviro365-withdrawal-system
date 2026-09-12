package com.enviro.assessment.junior.mutshutshudzi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Enviro365 Investments withdrawal notice system.
 *
 * This service lets investors view their portfolio, submit withdrawal
 * notices against a product balance, and export withdrawal history as CSV.
 */
@SpringBootApplication
public class WithdrawalSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithdrawalSystemApplication.class, args);
    }
}
