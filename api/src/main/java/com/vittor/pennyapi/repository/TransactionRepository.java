package com.vittor.pennyapi.repository;

import com.vittor.pennyapi.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByUserId(UUID userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    boolean existsByCategoryId(UUID categoryId);

    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as totalIncome, " +
           "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as totalExpense " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.date >= :startDate " +
           "AND t.date <= :endDate")
    java.util.List<Object[]> calculateFinancialSummary(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
