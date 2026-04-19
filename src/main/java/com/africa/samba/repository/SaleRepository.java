package com.africa.samba.repository;

import com.africa.samba.entity.Sale;
import com.africa.samba.entity.CashRegisterSession;
import com.africa.samba.entity.StoreProduct;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByCashRegisterSession(CashRegisterSession session);
    Page<Sale> findByCashRegisterSession(CashRegisterSession session, Pageable pageable);
    List<Sale> findByStoreProduct(StoreProduct storeProduct);
    List<Sale> findBySoldAtBetween(LocalDateTime start, LocalDateTime end);
    // Stats avancées
    List<Sale> findByCashRegisterSession_CashRegister_Id(UUID cashRegisterId);
    List<Sale> findByCashRegisterSession_CashRegister_Store_Id(UUID storeId);
    List<Sale> findByCashRegisterSession_User_IdAndCashRegisterSession_CashRegister_Store_Id(UUID userId, UUID storeId);
    List<Sale> findByCashRegisterSession_CashRegister_Store_IdAndSoldAtBetween(UUID storeId, LocalDateTime start, LocalDateTime end);
}
