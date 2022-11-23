package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {


    Integer countByEventIdAndStatus(Long id, RequestStatus requestStatus);

    List<Request> findByRequesterId(long userId);
    Request findRequestByEventId(long eventId);
    Request findRequestByEventIdAndRequesterId(long eventId, long requestorId);


}
