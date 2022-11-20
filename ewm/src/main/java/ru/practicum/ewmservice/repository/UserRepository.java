package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.model.user.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u.* " +
            "FROM users AS u " +
            "WHERE u.id IN ?1 " +
            "ORDER BY u.id ASC", nativeQuery = true)
    Page<User> findUsersById(List<Long> ids, Pageable pageable);
}
