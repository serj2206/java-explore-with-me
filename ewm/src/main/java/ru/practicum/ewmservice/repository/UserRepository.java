package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
