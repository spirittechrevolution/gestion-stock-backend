package com.africa.samba.controllers;

import com.africa.samba.common.constants.Constants;
import com.africa.samba.common.util.CustomResponse;
import com.africa.samba.dto.response.AuditLogResponse;
import com.africa.samba.mapper.AuditLogMapper;
import com.africa.samba.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Audit Log", description = "Consultation de l'historique des actions")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Operation(
        summary = "Lister l'historique d'une supérette",
        description = "Rôle requis : ADMIN ou OWNER. Retourne la liste paginée des actions effectuées dans la supérette.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historique",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = AuditLogResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token absent ou invalide"),
        @ApiResponse(responseCode = "403", description = "Accès refusé – rôle ADMIN ou OWNER requis")
    })
    @GetMapping("/store/{storeId}")
    public ResponseEntity<CustomResponse> listByStore(
            @PathVariable UUID storeId,
            Pageable pageable,
            HttpServletRequest httpRequest) {
        Page<AuditLogResponse> page = auditLogRepository.findByStoreId(storeId, pageable)
            .map(auditLogMapper::toResponse);
        return ResponseEntity.ok(new CustomResponse(
                Constants.Message.SUCCESS_BODY,
                Constants.Status.OK,
                "AUDIT_LOG_LIST_SUCCESS",
                page));
    }
}
