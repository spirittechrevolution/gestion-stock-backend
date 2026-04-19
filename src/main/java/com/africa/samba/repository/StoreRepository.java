package com.africa.samba.repository;

import com.africa.samba.entity.Store;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

  Page<Store> findByOwnerIdAndActiveTrue(UUID ownerId, Pageable pageable);

  Page<Store> findByActiveTrue(Pageable pageable);

  boolean existsByNameAndOwnerId(String name, UUID ownerId);
}
