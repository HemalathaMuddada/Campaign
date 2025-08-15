package com.xtremand.domain.entity;

import com.xtremand.domain.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "xt_roles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_xt_roles_name", columnNames = "name")
    },
    indexes = {
        @Index(name = "idx_xt_roles_created_at", columnList = "created_at"),
        @Index(name = "idx_xt_roles_updated_at", columnList = "updated_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;
}
