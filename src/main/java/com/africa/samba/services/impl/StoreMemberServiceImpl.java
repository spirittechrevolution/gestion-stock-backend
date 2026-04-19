package com.africa.samba.services.impl;

import com.africa.samba.codeLists.StoreMemberRole;
import com.africa.samba.common.constants.ResponseMessageConstants;
import com.africa.samba.common.exception.ConflictException;
import com.africa.samba.common.exception.CustomException;
import com.africa.samba.common.exception.NotFoundException;
import com.africa.samba.dto.request.AddStoreMemberRequest;
import com.africa.samba.dto.request.UpdateStoreMemberRequest;
import com.africa.samba.dto.response.StoreMemberResponse;
import com.africa.samba.entity.Store;
import com.africa.samba.entity.StoreMember;
import com.africa.samba.entity.User;
import com.africa.samba.mapper.StoreMemberMapper;
import com.africa.samba.repository.StoreMemberRepository;
import com.africa.samba.repository.StoreRepository;
import com.africa.samba.repository.UserRepository;
import com.africa.samba.services.interfaces.StoreMemberService;
import java.util.List;
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
public class StoreMemberServiceImpl implements StoreMemberService {

  private final StoreMemberRepository storeMemberRepository;
  private final StoreRepository storeRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public StoreMemberResponse add(UUID storeId, AddStoreMemberRequest request)
      throws CustomException {
    try {
      Store store =
          storeRepository
              .findById(storeId)
              .orElseThrow(
                  () -> new NotFoundException("Supérette introuvable avec l'id : " + storeId));

      User user =
          userRepository
              .findById(request.getUserId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Utilisateur introuvable avec l'id : " + request.getUserId()));

      if (storeMemberRepository.existsByStoreIdAndUserId(storeId, request.getUserId())) {
        throw new ConflictException("Cet utilisateur est déjà membre de cette supérette");
      }

      StoreMember member =
          StoreMember.builder().store(store).user(user).role(request.getRole()).build();

      StoreMember saved = storeMemberRepository.save(member);
      log.info(
          "Membre ajouté : storeId={}, userId={}, role={}",
          storeId,
          request.getUserId(),
          request.getRole());
      return StoreMemberMapper.toResponse(saved);
    } catch (NotFoundException | ConflictException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new CustomException(ex, ResponseMessageConstants.STORE_MEMBER_ADD_FAILURE);
    }
  }

  @Override
  @Transactional
  public StoreMemberResponse update(UUID storeId, UUID memberId, UpdateStoreMemberRequest request)
      throws CustomException {
    StoreMember member =
        storeMemberRepository
            .findById(memberId)
            .filter(m -> m.getStore().getId().equals(storeId))
            .orElseThrow(() -> new NotFoundException("Membre introuvable : memberId=" + memberId));

    if (request.getRole() != null) member.setRole(request.getRole());
    if (request.getActive() != null) member.setActive(request.getActive());

    StoreMember saved = storeMemberRepository.save(member);
    log.info("Membre mis à jour : id={}", saved.getId());
    return StoreMemberMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public void remove(UUID storeId, UUID memberId) throws CustomException {
    StoreMember member =
        storeMemberRepository
            .findById(memberId)
            .filter(m -> m.getStore().getId().equals(storeId))
            .orElseThrow(() -> new NotFoundException("Membre introuvable : memberId=" + memberId));
    member.setActive(false);
    storeMemberRepository.save(member);
    log.info("Membre désactivé : id={}", memberId);
  }

  @Override
  public Page<StoreMemberResponse> listByStore(UUID storeId, Pageable pageable) {
    return storeMemberRepository
        .findByStoreIdAndActiveTrue(storeId, pageable)
        .map(StoreMemberMapper::toResponse);
  }

  @Override
  public Page<StoreMemberResponse> listByRole(UUID storeId, StoreMemberRole role, Pageable pageable) {
    return storeMemberRepository
        .findByStoreIdAndRoleAndActiveTrue(storeId, role, pageable)
        .map(StoreMemberMapper::toResponse);
  }

  @Override
  public List<StoreMemberResponse> listByUser(UUID userId) {
    return storeMemberRepository.findByUserIdAndActiveTrue(userId).stream()
        .map(StoreMemberMapper::toResponse)
        .toList();
  }

  @Override
  public boolean isMember(UUID storeId, UUID userId) {
    return storeMemberRepository
        .findByStoreIdAndUserId(storeId, userId)
        .map(StoreMember::getActive)
        .orElse(false);
  }
}
