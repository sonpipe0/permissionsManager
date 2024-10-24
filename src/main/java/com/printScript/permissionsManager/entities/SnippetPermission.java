package com.printScript.permissionsManager.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "snippet_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SnippetPermission {
    @Id
    private String snippetId;

    @OneToMany(mappedBy = "snippetPermission", cascade = CascadeType.ALL)
    private List<UserGrantType> userGrantTypes;
}