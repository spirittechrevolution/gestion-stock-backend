package com.africa.samba.repository;

import com.africa.samba.codeLists.StoreMemberRole;
import com.africa.samba.entity.StoreMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreMemberRepository extends JpaRepository<StoreMember, UUID> {

  Page<StoreMember> findByStoreIdAndActiveTrue(UUID storeId, Pageable pageable);

  List<StoreMember> findByUserIdAndActiveTrue(UUID userId);

  Optional<StoreMember> findByStoreIdAndUserId(UUID storeId, UUID userId);

  boolean existsByStoreIdAndUserId(UUID storeId, UUID userId);

  Page<StoreMember> findByStoreIdAndRoleAndActiveTrue(
      UUID storeId, StoreMemberRole role, Pageable pageable);
}
