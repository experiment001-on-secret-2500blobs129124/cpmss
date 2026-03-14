package com.cpmss.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_role")
public class AppRole {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
