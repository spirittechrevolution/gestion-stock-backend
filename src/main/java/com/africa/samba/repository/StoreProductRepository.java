package com.africa.samba.repository;

import com.africa.samba.entity.StoreProduct;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreProductRepository extends JpaRepository<StoreProduct, UUID> {

  Page<StoreProduct> findByStoreIdAndActiveTrue(UUID storeId, Pageable pageable);

  Optional<StoreProduct> findByStoreIdAndProductId(UUID storeId, UUID productId);

  boolean existsByStoreIdAndProductId(UUID storeId, UUID productId);

  @Query(
      "SELECT sp FROM StoreProduct sp WHERE sp.store.id = :storeId "
          + "AND sp.active = true AND sp.stock <= sp.stockMin")
  Page<StoreProduct> findLowStock(@Param("storeId") UUID storeId, Pageable pageable);

  @Query(
      "SELECT sp FROM StoreProduct sp JOIN sp.product p JOIN p.barcodes b "
          + "WHERE sp.store.id = :storeId AND b.code = :barcode")
  Optional<StoreProduct> findByStoreIdAndBarcode(
      @Param("storeId") UUID storeId, @Param("barcode") String barcode);
}
