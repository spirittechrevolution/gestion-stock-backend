package com.africa.samba.repository;

import com.africa.samba.codeLists.TypeQRCode;
import com.africa.samba.entity.QRCode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, UUID> {

  Optional<QRCode> findByProduitIdAndType(UUID produitId, TypeQRCode type);

  Optional<QRCode> findByBoutiqueIdAndType(UUID boutiqueId, TypeQRCode type);

  Optional<QRCode> findByPayload(String payload);

  boolean existsByProduitId(UUID produitId);
}
