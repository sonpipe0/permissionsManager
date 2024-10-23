package com.printScript.permissionsManager.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_grant_types")
@Getter
@Setter
@NoArgsConstructor
public class UserGrantType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "snippet_id", nullable = false)
    private SnippetPermission snippetPermission;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_type", nullable = false)
    private GrantType grantType; // "read" or "write"
}