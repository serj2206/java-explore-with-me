package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventRequestCount;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Page<Event> findEventByInitiatorId(long userId, Pageable pageable);

    @Query(value = "SELECT new ru.practicum.ewmservice.model.event.dto.EventRequestCount(e,  count (r.event.id)) " +
            "FROM Event AS e LEFT OUTER JOIN " +
            "Request AS r ON e.id = r.event.id " +
            "WHERE e IN ?1 AND r.status = 'CONFIRM' " +
            "GROUP BY e.id, r.event.id ")
    List<EventRequestCount> findEventByEventListRequestConfirm(List<Event> eventIdList);


    //Event fndOne(BooleanExpression eq);
}
