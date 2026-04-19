package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.AddCashRegisterRequest;
import com.africa.samba.dto.request.UpdateCashRegisterRequest;
import com.africa.samba.dto.response.CashRegisterResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CashRegisterService {
    CashRegisterResponse create(UUID storeId, AddCashRegisterRequest request);
    List<CashRegisterResponse> list(UUID storeId);
    Page<CashRegisterResponse> list(UUID storeId, Pageable pageable);
    CashRegisterResponse update(UUID storeId, UUID cashRegisterId, UpdateCashRegisterRequest request);
    void remove(UUID storeId, UUID cashRegisterId);
}
