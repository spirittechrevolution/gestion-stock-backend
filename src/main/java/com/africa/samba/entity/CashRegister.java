package com.africa.samba.entity;

import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "cash_registers",
    schema = "administrative",
    uniqueConstraints = @UniqueConstraint(name = "uk_store_cash_number", columnNames = {"store_id", "number"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CashRegister extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private Integer number;

    @Column(length = 50)
    private String label;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
