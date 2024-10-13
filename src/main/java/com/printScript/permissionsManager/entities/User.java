package com.printScript.permissionsManager.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true)
    private String userId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SnippetPermission> snippetPermissions;

}
