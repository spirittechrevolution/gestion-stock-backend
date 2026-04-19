package com.africa.samba.services.interfaces;

import com.africa.samba.dto.request.CreateSaleRequest;
import com.africa.samba.dto.response.SaleResponse;
import com.africa.samba.dto.response.SalesStatsResponse;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SaleService {
    SaleResponse create(UUID storeId, UUID cashRegisterId, UUID sessionId, CreateSaleRequest request, UUID sellerId);
    List<SaleResponse> listBySession(UUID sessionId);
    Page<SaleResponse> listBySession(UUID sessionId, Pageable pageable);
    SalesStatsResponse statsBySession(UUID sessionId);
    SalesStatsResponse statsByCashRegister(UUID cashRegisterId);
    SalesStatsResponse statsByStore(UUID storeId);
    SalesStatsResponse statsBySeller(UUID storeId, UUID sellerId);
    SalesStatsResponse statsByPeriod(UUID storeId, String periodType, String periodValue);
    // periodType: "day", "month", "year"; periodValue: "2026-04-19", "2026-04", "2026"
    // Pour les stats : chiffre d'affaires, nombre de ventes, marge, etc.
    // On pourra ajouter d'autres méthodes selon les besoins
    SalesStatsResponse statsByStoreProduct(UUID storeProductId);
    SalesStatsResponse statsByPeriodGlobal(LocalDateTime start, LocalDateTime end);
}
