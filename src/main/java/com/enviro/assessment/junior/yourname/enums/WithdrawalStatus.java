package com.enviro.assessment.junior.yourname.enums;

/**
 * Lifecycle status of a withdrawal notice.
 * In this system, a withdrawal is validated synchronously on submission:
 * it becomes APPROVED immediately if it passes every business rule, or
 * the request is rejected outright (a WithdrawalNotice row is never
 * persisted for a rejected request).
 */
public enum WithdrawalStatus {
    APPROVED,
    REJECTED
}
