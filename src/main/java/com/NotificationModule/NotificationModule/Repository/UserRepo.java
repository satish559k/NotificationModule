package com.NotificationModule.NotificationModule.Repository;

import com.NotificationModule.NotificationModule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID Id);
}
