package com.africa.samba.repository;

import com.africa.samba.codeLists.ProductStatus;
import com.africa.samba.entity.Product;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  Page<Product> findByActiveTrue(Pageable pageable);

  @Query(
      "SELECT p FROM Product p WHERE p.active = true "
          + "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

  Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

  boolean existsByNameAndBrand(String name, String brand);

  Page<Product> findByStatusAndCreatedByStoreId(
      ProductStatus status, UUID storeId, Pageable pageable);
}
