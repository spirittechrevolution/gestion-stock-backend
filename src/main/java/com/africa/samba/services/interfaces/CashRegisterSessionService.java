package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.OpenCashRegisterSessionRequest;
import com.africa.samba.dto.response.CashRegisterSessionResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CashRegisterSessionService {
    CashRegisterSessionResponse open(UUID storeId, UUID cashRegisterId, OpenCashRegisterSessionRequest request, UUID openedById);
    CashRegisterSessionResponse close(UUID storeId, UUID cashRegisterId, UUID sessionId, UUID closedById);
    List<CashRegisterSessionResponse> list(UUID storeId, UUID cashRegisterId);
    Page<CashRegisterSessionResponse> list(UUID storeId, UUID cashRegisterId, Pageable pageable);
}
