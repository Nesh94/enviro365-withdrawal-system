package com.enviro.assessment.junior.mutshutshudzi.repository;

import com.enviro.assessment.junior.mutshutshudzi.entity.WithdrawalNotice;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalStatus;
import com.enviro.assessment.junior.mutshutshudzi.enums.WithdrawalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    List<WithdrawalNotice> findByInvestorIdOrderByRequestDateDesc(Long investorId);

    /**
     * Backs both the history table and the CSV export. Every filter is
     * optional (pass null to skip it) so the same query serves an
     * unfiltered "give me everything" call and a fully filtered one.
     */
    @Query("""
            SELECT w FROM WithdrawalNotice w
            WHERE (:investorId IS NULL OR w.investor.id = :investorId)
              AND (:status IS NULL OR w.status = :status)
              AND (:withdrawalType IS NULL OR w.withdrawalType = :withdrawalType)
              AND (:startDate IS NULL OR w.requestDate >= :startDate)
              AND (:endDate IS NULL OR w.requestDate <= :endDate)
            ORDER BY w.requestDate DESC
            """)
    List<WithdrawalNotice> findWithFilters(
            @Param("investorId") Long investorId,
            @Param("status") WithdrawalStatus status,
            @Param("withdrawalType") WithdrawalType withdrawalType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
