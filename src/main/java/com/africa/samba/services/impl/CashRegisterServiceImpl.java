package com.africa.samba.services.impl;

import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.BadRequestException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.AddCashRegisterRequest;
import com.africa.samba.dto.request.UpdateCashRegisterRequest;
import com.africa.samba.dto.response.CashRegisterResponse;
import com.africa.samba.entity.CashRegister;
import com.africa.samba.entity.Store;
import com.africa.samba.mapper.CashRegisterMapper;
import com.africa.samba.repository.CashRegisterRepository;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.services.interfaces.CashRegisterService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashRegisterServiceImpl implements CashRegisterService {
    private final CashRegisterRepository cashRegisterRepository;
    private final StoreRepository storeRepository;
    private final CashRegisterMapper cashRegisterMapper;

    @Override
    @Transactional
    public CashRegisterResponse create(UUID storeId, AddCashRegisterRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        if (cashRegisterRepository.findByStoreAndNumber(store, request.getNumber()).isPresent()) {
            throw new BadRequestException(ResponseMessageConstants.CASH_REGISTER_ALREADY_EXISTS);
        }
        CashRegister entity = CashRegister.builder()
                .store(store)
                .number(request.getNumber())
                .label(request.getLabel())
                .active(true)
                .build();
        cashRegisterRepository.save(entity);
        return cashRegisterMapper.toResponse(entity);
    }

    @Override
    public List<CashRegisterResponse> list(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        return cashRegisterRepository.findByStoreAndActiveTrue(store)
                .stream().map(cashRegisterMapper::toResponse).toList();
    }

    @Override
    public Page<CashRegisterResponse> list(UUID storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        return cashRegisterRepository.findByStoreAndActiveTrue(store, pageable)
                .map(cashRegisterMapper::toResponse);
    }

    @Override
    @Transactional
    public CashRegisterResponse update(UUID storeId, UUID cashRegisterId, UpdateCashRegisterRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegister entity = cashRegisterRepository.findById(cashRegisterId)
                .filter(c -> c.getStore().equals(store))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_NOT_FOUND));
        entity.setLabel(request.getLabel());
        entity.setActive(request.getActive());
        cashRegisterRepository.save(entity);
        return cashRegisterMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void remove(UUID storeId, UUID cashRegisterId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.STORE_MEMBER_NOT_FOUND));
        CashRegister entity = cashRegisterRepository.findById(cashRegisterId)
                .filter(c -> c.getStore().equals(store))
                .orElseThrow(() -> new NotFoundException(ResponseMessageConstants.CASH_REGISTER_NOT_FOUND));
        entity.setActive(false);
        cashRegisterRepository.save(entity);
    }
}
