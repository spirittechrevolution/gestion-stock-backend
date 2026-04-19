package com.africa.samba.dto.response;

import com.africa.samba.codeLists.StoreMemberRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record StoreMemberResponse(
    UUID id,
    UUID storeId,
    String storeName,
    UUID userId,
    String userFullName,
    String userEmail,
    StoreMemberRole role,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
