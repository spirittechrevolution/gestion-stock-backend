package com.africa.samba.services.impl;

import com.africa.samba.services.util.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.BadRequestException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.OpenCashRegisterSessionRequest;
import com.africa.samba.dto.response.CashRegisterSessionResponse;
import com.africa.samba.entity.CashRegister;
import com.africa.samba.entity.CashRegisterSession;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.User;
import com.africa.samba.mapper.CashRegisterSessionMapper;
import com.africa.samba.repository.CashRegisterRepository;
import com.africa.samba.repository.CashRegisterSessionRepository;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.CashRegisterSessionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashRegisterSessionServiceImpl implements CashRegisterSessionService {
    private final CashRegisterRepository cashRegisterRepository;
    private final CashRegisterSessionRepository sessionRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final CashRegisterSessionMapper sessionMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CashRegisterSessionResponse open(UUID storeId, UUID cashRegisterId, OpenCashRegisterSessionRequest request, UUID openedById) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegister cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .filter(c -> c.getStore().equals(store))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_NOT_FOUND));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.USER_NOT_FOUND));
        User openedBy = userRepository.findById(openedById)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.USER_NOT_FOUND));
        // Vérifier qu'il n'y a pas déjà une session ouverte sur cette caisse
        if (sessionRepository.findByCashRegisterAndClosedAtIsNull(cashRegister).isPresent()) {
            throw new BadRequestException(ResponseMessageConstants.CASH_REGISTER_SESSION_ALREADY_OPEN);
        }
        // Vérifier que ce vendeur n'a pas déjà une session ouverte dans cette supérette
        if (sessionRepository.findByUserAndCashRegister_Store_IdAndClosedAtIsNull(user, storeId).isPresent()) {
            throw new BadRequestException(ResponseMessageConstants.CASH_REGISTER_SESSION_USER_ALREADY_ASSIGNED);
        }
        CashRegisterSession session = CashRegisterSession.builder()
                .cashRegister(cashRegister)
                .user(user)
                .openedBy(openedBy)
                .openedAt(LocalDateTime.now())
                .build();
        sessionRepository.save(session);
        auditLogService.log(
            "SESSION_OPEN",
            openedById,
            storeId,
            cashRegisterId,
            session.getId(),
            null,
            String.format("{\"userId\":\"%s\"}", user.getId())
        );
        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public CashRegisterSessionResponse close(UUID storeId, UUID cashRegisterId, UUID sessionId, UUID closedById) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegister cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .filter(c -> c.getStore().equals(store))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_NOT_FOUND));
        CashRegisterSession session = sessionRepository.findById(sessionId)
                .filter(s -> s.getCashRegister().equals(cashRegister))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_SESSION_NOT_FOUND));
        if (session.getClosedAt() != null) {
            throw new BadRequestException(ResponseMessageConstants.CASH_REGISTER_SESSION_NOT_FOUND);
        }
        session.setClosedAt(LocalDateTime.now());
        sessionRepository.save(session);
        auditLogService.log(
            "SESSION_CLOSE",
            closedById,
            storeId,
            cashRegisterId,
            session.getId(),
            null,
            null
        );
        return sessionMapper.toResponse(session);
    }

    @Override
    public List<CashRegisterSessionResponse> list(UUID storeId, UUID cashRegisterId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegister cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .filter(c -> c.getStore().equals(store))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_NOT_FOUND));
        return sessionRepository.findByCashRegister(cashRegister)
                .stream().map(sessionMapper::toResponse).toList();
    }

    @Override
    public Page<CashRegisterSessionResponse> list(UUID storeId, UUID cashRegisterId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegister cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .filter(c -> c.getStore().equals(store))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_NOT_FOUND));
        return sessionRepository.findByCashRegister(cashRegister, pageable)
                .map(sessionMapper::toResponse);
    }
}
