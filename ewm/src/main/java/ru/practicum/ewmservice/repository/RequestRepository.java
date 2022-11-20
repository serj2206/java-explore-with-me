package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.model.request.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findRequestById(long id);
}
