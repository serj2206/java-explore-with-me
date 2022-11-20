package ru.practicum.statsserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsserver.common.marker.Create;
import ru.practicum.statsserver.model.ViewStats;
import ru.practicum.statsserver.model.dto.EndpointHitDto;
import ru.practicum.statsserver.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    //Сохранение информации о том, что к эндпоинту был запрос
    @PostMapping("/hit")
    public void addStats(@Validated({Create.class}) @RequestBody EndpointHitDto endpointHitDto) {
        statsService.addStats(endpointHitDto);
        return;
    }

    @GetMapping("/stats")
    public List<ViewStats> findStats(@RequestParam String start,
                              @RequestParam String end,
                              @RequestParam List<String> uris,
                              @RequestParam(defaultValue = "false") Boolean unique) {

        return statsService.findStats(start, end, uris, unique);
    }

}
