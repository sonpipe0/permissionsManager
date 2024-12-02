package com.printScript.permissionsManager.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "snippet_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SnippetPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Id
    @Column(name = "snippetId", unique = true)
    private String snippetId;

    @Id
    @Column(name = "id", unique = true)
    private String userId;

    private GrantType GrantType;
}
