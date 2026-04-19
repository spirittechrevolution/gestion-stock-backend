package com.africa.samba.repository;

import com.africa.samba.entity.Barcode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, UUID> {

  Optional<Barcode> findByCode(String code);

  boolean existsByCode(String code);

  java.util.List<Barcode> findByProductId(UUID productId);

  /**
   * Retourne le code interne le plus élevé (préfixe '2', 13 chiffres). Permet de calculer le
   * prochain code séquentiel.
   */
  @Query("SELECT MAX(b.code) FROM Barcode b WHERE b.type = 'INTERNAL' AND b.code LIKE '2%'")
  Optional<String> findMaxInternalCode();
}
