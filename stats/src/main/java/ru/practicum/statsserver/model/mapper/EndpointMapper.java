package ru.practicum.statsserver.model.mapper;

import ru.practicum.statsserver.model.EndpointHit;
import ru.practicum.statsserver.model.ViewStats;
import ru.practicum.statsserver.model.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .timestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        return endpointHit;
    }

    public static ViewStats toViewsStats(EndpointHit endpointHit, int hit) {
        return new ViewStats(endpointHit.getApp(), endpointHit.getUri(), hit);
    }
}
