package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventRequestCount;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "SELECT new ru.practicum.ewmservice.model.event.dto.EventRequestCountDto(r.event, count (r.event)) " +
            "FROM Request AS r INNER JOIN Event AS e ON r.event = e " +
            "WHERE e.initiator = ?1 AND r.status = 'CONFIRM' " +
            "GROUP BY e.id")
    Page<EventRequestCount> findEventByInitiatorId(long initiatorId, Pageable pageable);

    Event findEventById(long eventId);

}
