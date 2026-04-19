package com.africa.samba.repository;

import com.africa.samba.entity.CashRegister;
import com.africa.samba.entity.CashRegisterSession;
import com.africa.samba.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashRegisterSessionRepository extends JpaRepository<CashRegisterSession, UUID> {
    Optional<CashRegisterSession> findByCashRegisterAndClosedAtIsNull(CashRegister cashRegister);
    List<CashRegisterSession> findByCashRegister(CashRegister cashRegister);
    Page<CashRegisterSession> findByCashRegister(CashRegister cashRegister, Pageable pageable);
    Optional<CashRegisterSession> findByUserAndCashRegister_Store_IdAndClosedAtIsNull(User user, UUID storeId);
}
