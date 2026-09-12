package com.enviro.assessment.junior.yourname.exception;

/**
 * Thrown when a withdrawal request violates one of Enviro365's business
 * rules (age restriction, insufficient balance, exceeds the 90% cap).
 */
public class InvalidWithdrawalException extends RuntimeException {
    public InvalidWithdrawalException(String message) {
        super(message);
    }
}
