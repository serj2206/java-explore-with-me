package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.model.event.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findEventById(long id);

}
