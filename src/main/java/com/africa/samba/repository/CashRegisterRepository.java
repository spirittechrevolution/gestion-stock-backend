package com.africa.samba.repository;

import com.africa.samba.entity.CashRegister;
import com.africa.samba.entity.Store;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, UUID> {
    List<CashRegister> findByStoreAndActiveTrue(Store store);
    Page<CashRegister> findByStoreAndActiveTrue(Store store, Pageable pageable);
    Optional<CashRegister> findByStoreAndNumber(Store store, Integer number);
}
