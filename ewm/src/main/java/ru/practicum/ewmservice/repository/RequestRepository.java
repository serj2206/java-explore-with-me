package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    Long countByEventIdAndStatus(Long id, RequestStatus requestStatus);

    List<Request> findByRequesterId(long userId);

    List<Request> findRequestByEventIdAndStatus(long eventId, RequestStatus requestStatus);

    List<Request> findRequestByEventId(long eventId);

    Request findRequestByEventIdAndRequesterId(long eventId, long requesterId);

}
