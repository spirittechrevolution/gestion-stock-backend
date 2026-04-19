package com.africa.samba.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoreResponse(
    UUID id,
    String name,
    String address,
    String phone,
    boolean active,
    UUID ownerId,
    String ownerFullName,
    long productCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
