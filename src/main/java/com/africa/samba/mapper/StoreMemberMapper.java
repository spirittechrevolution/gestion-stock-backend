package com.africa.samba.mapper;

import com.africa.samba.dto.response.StoreMemberResponse;
import com.africa.samba.entity.StoreMember;
import org.springframework.stereotype.Component;

@Component
public class StoreMemberMapper {

  private StoreMemberMapper() {}

  public static StoreMemberResponse toResponse(StoreMember member) {
    return new StoreMemberResponse(
        member.getId(),
        member.getStore().getId(),
        member.getStore().getName(),
        member.getUser().getId(),
        member.getUser().getFullName(),
        member.getUser().getEmail(),
        member.getRole(),
        member.getActive(),
        member.getCreatedAt(),
        member.getUpdatedAt());
  }
}
