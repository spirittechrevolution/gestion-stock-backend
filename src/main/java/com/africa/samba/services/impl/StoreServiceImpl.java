package com.africa.samba.services.impl;

import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.ConflictException;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.CreateStoreRequest;
import com.africa.samba.dto.request.UpdateStoreRequest;
import com.africa.samba.dto.response.StoreResponse;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.User;
import com.africa.samba.mapper.StoreMapper;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.StoreService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {

  private final StoreRepository storeRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public StoreResponse create(UUID ownerId, CreateStoreRequest request) throws CustomException {
    try {
      User owner =
          userRepository
              .findById(ownerId)
              .orElseThrow(
                  () -> new NotFoundException("Propriétaire introuvable avec l'id : " + ownerId));

      if (storeRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
        throw new ConflictException("Une supérette avec ce nom existe déjà pour ce propriétaire");
      }

      Store store =
          Store.builder()
              .name(request.getName())
              .address(request.getAddress())
              .phone(request.getPhone())
              .owner(owner)
              .build();

      Store saved = storeRepository.save(store);
      log.info(
          "Supérette créée : id={}, name={}, owner={}", saved.getId(), saved.getName(), ownerId);
      return StoreMapper.toResponse(saved);
    } catch (NotFoundException | ConflictException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CustomException(ex, ResponseMessageConstants.STORE_CREATE_FAILURE);
    }
  }

  @Override
  public StoreResponse getById(UUID id) throws CustomException {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Supérette introuvable avec l'id : " + id));
    return StoreMapper.toResponse(store);
  }

  @Override
  public Page<StoreResponse> listByOwner(UUID ownerId, Pageable pageable) {
    return storeRepository
        .findByOwnerIdAndActiveTrue(ownerId, pageable)
        .map(StoreMapper::toResponse);
  }

  @Override
  public Page<StoreResponse> listAll(Pageable pageable) {
    return storeRepository.findByActiveTrue(pageable).map(StoreMapper::toResponse);
  }

  @Override
  @Transactional
  public StoreResponse update(UUID id, UpdateStoreRequest request) throws CustomException {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Supérette introuvable avec l'id : " + id));

    if (request.getName() != null) store.setName(request.getName());
    if (request.getAddress() != null) store.setAddress(request.getAddress());
    if (request.getPhone() != null) store.setPhone(request.getPhone());
    if (request.getActive() != null) store.setActive(request.getActive());

    Store saved = storeRepository.save(store);
    log.info("Supérette mise à jour : id={}", saved.getId());
    return StoreMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public void delete(UUID id) throws CustomException {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Supérette introuvable avec l'id : " + id));
    store.setActive(false);
    storeRepository.save(store);
    log.info("Supérette désactivée : id={}", id);
  }
}
