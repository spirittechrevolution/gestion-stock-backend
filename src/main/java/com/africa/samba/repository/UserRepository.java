package com.africa.samba.repository;

import com.africa.samba.codeLists.Role;
import com.africa.samba.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByPhone(String phone);

  Optional<User> findByKeycloakId(String keycloakId);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles")
  List<User> findAdminUsers(@Param("roles") Set<Role> roles);

  /** Mise à jour de la date de dernière connexion */
  @Modifying
  @Query("UPDATE User u SET u.lastLoginAt = :date WHERE u.id = :id")
  void updateDerniereConnexion(@Param("id") UUID id, @Param("date") LocalDateTime date);

  /** Invalidation du token de session */
  @Modifying
  @Query("UPDATE User u SET u.tokenSession = NULL WHERE u.id = :id")
  void clearTokenSession(@Param("id") UUID id);
}
