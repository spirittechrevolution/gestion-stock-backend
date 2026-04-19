package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.AddStoreMemberRequest;
import com.africa.samba.dto.request.UpdateStoreMemberRequest;
import com.africa.samba.dto.response.StoreMemberResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreMemberService {

  StoreMemberResponse add(UUID storeId, AddStoreMemberRequest request) throws CustomException;

  StoreMemberResponse update(UUID storeId, UUID memberId, UpdateStoreMemberRequest request)
      throws CustomException;

  void remove(UUID storeId, UUID memberId) throws CustomException;

  Page<StoreMemberResponse> listByStore(UUID storeId, Pageable pageable);

  boolean isMember(UUID storeId, UUID userId);
}
