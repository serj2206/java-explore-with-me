package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.model.ViewStats;
import ru.practicum.statsserver.model.dto.EndpointHitDto;
import ru.practicum.statsserver.model.mapper.EndpointMapper;
import ru.practicum.statsserver.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatisticsRepository statisticsRepository;

    public void addStats(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointMapper.toEndpointHit(endpointHitDto);
        statisticsRepository.save(endpointHit);
        return;
    }

    public List<ViewStats> findStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startDateTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDateTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<ViewStats> viewStatsList = null;

        if (!unique) {
            viewStatsList = statisticsRepository.findNotUniqueIP(startDateTime, endDateTime, uris);
        } else if(unique) {
            viewStatsList = statisticsRepository.findUniqueIp(startDateTime, endDateTime, uris);
        }
        return viewStatsList;
    }
}
