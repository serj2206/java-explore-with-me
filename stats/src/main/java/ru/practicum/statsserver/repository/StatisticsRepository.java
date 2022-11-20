package ru.practicum.statsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.statsserver.model.EndpointHit;

public interface StatisticsRepository extends JpaRepository<EndpointHit, Long> {
}
