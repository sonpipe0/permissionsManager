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
    private String snippetId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
