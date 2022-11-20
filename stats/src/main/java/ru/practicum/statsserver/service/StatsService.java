package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statsserver.model.ViewStats;
import ru.practicum.statsserver.model.dto.EndpointHitDto;
import ru.practicum.statsserver.repository.StatisticsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatisticsRepository statisticsRepository;

    public void addStats(EndpointHitDto endpointHitDto) {
        return;
    }

    public List<ViewStats> findStats(String start, String end, List<String> uris, boolean unique) {

        return null;
    }
}
