package com.africa.samba.services.impl;

import com.africa.samba.services.util.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.BadRequestException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.CreateSaleRequest;
import com.africa.samba.dto.response.SaleResponse;
import com.africa.samba.dto.response.SalesStatsResponse;
import com.africa.samba.entity.CashRegisterSession;
import com.africa.samba.entity.Sale;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.StoreProduct;
import com.africa.samba.mapper.SaleMapper;
import com.africa.samba.repository.CashRegisterRepository;
import com.africa.samba.repository.CashRegisterSessionRepository;
import com.africa.samba.repository.SaleRepository;
import com.africa.samba.repository.StoreProductRepository;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.SaleService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final CashRegisterSessionRepository sessionRepository;
    private final StoreProductRepository storeProductRepository;
    private final StoreRepository storeRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final UserRepository userRepository;
    private final SaleMapper saleMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SaleResponse create(UUID storeId, UUID cashRegisterId, UUID sessionId, CreateSaleRequest request, UUID sellerId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegisterSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_SESSION_NOT_FOUND));
        if (session.getClosedAt() != null) {
            throw new BadRequestException("Session de caisse déjà clôturée");
        }
        StoreProduct storeProduct = storeProductRepository.findById(request.getStoreProductId())
                .orElseThrow(() -> new NotFoundException("Produit non trouvé dans la supérette"));
        if (!storeProduct.getStore().equals(store)) {
            throw new BadRequestException("Produit non lié à cette supérette");
        }
        if (storeProduct.getStock() < request.getQuantity()) {
            throw new BadRequestException("Stock insuffisant");
        }
        // Décrémenter le stock
        storeProduct.decrementerStock(request.getQuantity());
        storeProductRepository.save(storeProduct);
        // Calcul du total
        BigDecimal total = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        Sale sale = Sale.builder()
                .cashRegisterSession(session)
                .storeProduct(storeProduct)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .totalPrice(total)
                .soldAt(LocalDateTime.now())
                .build();
        saleRepository.save(sale);
        auditLogService.log(
                "SALE_CREATE",
                sellerId,
                storeId,
                cashRegisterId,
                sessionId,
                sale.getId(),
                String.format("{\"quantity\":%d,\"unitPrice\":%s,\"totalPrice\":%s}", request.getQuantity(), request.getUnitPrice(), total)
        );
        return saleMapper.toResponse(sale);
    }

    @Override
    public List<SaleResponse> listBySession(UUID sessionId) {
        CashRegisterSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_SESSION_NOT_FOUND));
        return saleRepository.findByCashRegisterSession(session)
                .stream().map(saleMapper::toResponse).toList();
    }

    @Override
    public Page<SaleResponse> listBySession(UUID storeId, UUID cashRegisterId, UUID sessionId, Pageable pageable) {
        CashRegisterSession session = sessionRepository
                .findByIdAndCashRegister_IdAndCashRegister_Store_Id(sessionId, cashRegisterId, storeId)
                .orElseThrow(() -> new NotFoundException(
                        "Session introuvable pour la caisse " + cashRegisterId + " de la supérette " + storeId));
        return saleRepository.findByCashRegisterSession(session, pageable)
                .map(saleMapper::toResponse);
    }

    @Override
    public SalesStatsResponse statsBySession(UUID sessionId) {
        List<Sale> sales = saleRepository.findByCashRegisterSession(
                sessionRepository.findById(sessionId).orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_SESSION_NOT_FOUND))
        );
        return computeStats(sales);
    }

    @Override
    public SalesStatsResponse statsByCashRegister(UUID cashRegisterId) {
        // Toutes les ventes de toutes les sessions de cette caisse
        var sales = saleRepository.findByCashRegisterSession_CashRegister_Id(cashRegisterId);
        return computeStats(sales);
    }

    @Override
    public SalesStatsResponse statsByStore(UUID storeId) {
        // Toutes les ventes de toutes les sessions de toutes les caisses de la supérette
        var sales = saleRepository.findByCashRegisterSession_CashRegister_Store_Id(storeId);
        return computeStats(sales);
    }

    @Override
    public SalesStatsResponse statsBySeller(UUID storeId, UUID sellerId) {
        // Toutes les ventes faites par ce vendeur dans la supérette (sessions où user = sellerId)
        var sales = saleRepository.findByCashRegisterSession_User_IdAndCashRegisterSession_CashRegister_Store_Id(sellerId, storeId);
        return computeStats(sales);
    }

    @Override
    public SalesStatsResponse statsByPeriod(UUID storeId, String periodType, String periodValue) {
        // Période: day=YYYY-MM-DD, month=YYYY-MM, year=YYYY
        LocalDateTime start;
        LocalDateTime end;
        switch (periodType.toLowerCase()) {
            case "day" -> {
                var date = LocalDate.parse(periodValue);
                start = date.atStartOfDay();
                end = date.plusDays(1).atStartOfDay();
            }
            case "month" -> {
                var ym = YearMonth.parse(periodValue);
                start = ym.atDay(1).atStartOfDay();
                end = ym.plusMonths(1).atDay(1).atStartOfDay();
            }
            case "year" -> {
                var year = Year.parse(periodValue);
                start = year.atDay(1).atStartOfDay();
                end = year.plusYears(1).atDay(1).atStartOfDay();
            }
            default -> throw new IllegalArgumentException("Type de période inconnu : " + periodType);
        }
        var sales = saleRepository.findByCashRegisterSession_CashRegister_Store_IdAndSoldAtBetween(storeId, start, end);
        return computeStats(sales);
    }

    /**
     * Statistiques pour un produit précis dans une supérette (StoreProduct).
     */
    public SalesStatsResponse statsByStoreProduct(UUID storeId, UUID storeProductId) {
        var storeProduct = storeProductRepository.findByIdAndStoreId(storeProductId, storeId)
                .orElseThrow(() -> new NotFoundException(
                        "Produit introuvable dans la supérette " + storeId));
        var sales = saleRepository.findByStoreProduct(storeProduct);
        return computeStats(sales);
    }

    /**
     * Statistiques globales sur toutes les ventes de toutes les supérettes sur une période donnée.
     */
    public SalesStatsResponse statsByPeriodGlobal(LocalDateTime start, LocalDateTime end) {
        var sales = saleRepository.findBySoldAtBetween(start, end);
        return computeStats(sales);
    }

    private SalesStatsResponse computeStats(List<Sale> sales) {
        long count = sales.size();
        BigDecimal revenue = sales.stream().map(Sale::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal margin = sales.stream().map(sale -> {
            BigDecimal cost = sale.getStoreProduct().getCostPrice();
            if (cost == null) return BigDecimal.ZERO;
            return sale.getUnitPrice().subtract(cost).multiply(BigDecimal.valueOf(sale.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        return SalesStatsResponse.builder()
                .salesCount(count)
                .totalRevenue(revenue)
                .totalMargin(margin)
                .build();
    }
}
