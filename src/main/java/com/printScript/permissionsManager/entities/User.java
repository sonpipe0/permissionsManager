package com.printScript.permissionsManager.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id", unique = true)
    private String userId;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private Integer hash;

    @Column
    private String salt;

    @Column
    private String role;
}
