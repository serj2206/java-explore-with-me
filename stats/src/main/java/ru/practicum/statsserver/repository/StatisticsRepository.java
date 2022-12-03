package ru.practicum.statsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.statsserver.model.ViewStats(s.app, s.uri, COUNT (s.ip)) " +
            "FROM EndpointHit AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri ")
    List<ViewStats> findNotUniqueIP(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.statsserver.model.ViewStats(s.app, s.uri, COUNT (DISTINCT s.ip)) " +
            "FROM EndpointHit AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri")
    List<ViewStats> findUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}




