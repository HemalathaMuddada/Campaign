package com.xtremand.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "xt_users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_xt_users_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_xt_users_email", columnList = "email"),
        @Index(name = "idx_xt_users_role_id", columnList = "role_id"),
        @Index(name = "idx_xt_users_created_at", columnList = "created_at"),
        @Index(name = "idx_xt_users_updated_at", columnList = "updated_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
}
