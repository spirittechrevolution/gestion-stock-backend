package com.africa.samba.repository;

import com.africa.samba.entity.Barcode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, UUID> {

  Optional<Barcode> findByCode(String code);

  boolean existsByCode(String code);

  java.util.List<Barcode> findByProductId(UUID productId);
}
