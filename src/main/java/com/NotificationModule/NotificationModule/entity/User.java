package com.NotificationModule.NotificationModule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false,name = "user_id")
    private UUID Id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @CreationTimestamp
    @Column(updatable = false,name = "created_at")
    private Timestamp createdAt;
}
