package com.africa.samba.repository;

import com.africa.samba.entity.LaCodeList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface LaCodeListRepository extends CrudRepository<LaCodeList, UUID> {

  List<LaCodeList> findAllByType(String type);

  Page<LaCodeList> findAll(Pageable pageable);
}
