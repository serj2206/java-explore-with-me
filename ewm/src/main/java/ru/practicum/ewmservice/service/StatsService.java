package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.client.PublicClient;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.statistic.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatsService {

    private final PublicClient publicClient;

    public Integer getViewsForOneEvent(Event event, boolean unique) {
        String uris = "/event/" + event.getId();
        String start = event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String end = event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStats> viewStatsList = publicClient.findStats(start, end, uris, unique);

        if (viewStatsList == null) return null;
        Integer views = 0;
        for (ViewStats vs : viewStatsList) {
            views += vs.getHits();
        }
        return views;
    }

    public List<ViewStats> getViewsStatsListByEventShort(List<EventShortDto> eventShortDtoList) {
        String start = eventShortDtoList.stream()
                .min(Comparator.comparing(even -> even.getEventDate()))
                .get()
                .getEventDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder urisBuilder = new StringBuilder();
        for (EventShortDto eventShortDto : eventShortDtoList) {
            urisBuilder.append("/event/").append(eventShortDto.getId()).append(", ");
        }
        String uris = urisBuilder.toString();
        //Запрос списка статистической информации
        List<ViewStats> viewStatsList = publicClient.findStats(
                start,
                end,
                uris,
                true);
        return viewStatsList;
    }

    public List<ViewStats> getViewsStatsListByEventFull(List<EventFullDto> eventFullDtoList) {
        String start = eventFullDtoList.stream()
                .min(Comparator.comparing(even -> even.getEventDate()))
                .get()
                .getEventDate();

        String end = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder urisBuilder = new StringBuilder();
        for (EventFullDto eventFullDto : eventFullDtoList) {
            urisBuilder.append("/event/").append(eventFullDto.getId()).append(", ");
        }
        String uris = urisBuilder.toString();
        //Запрос списка статистической информации
        List<ViewStats> viewStatsList = publicClient.findStats(
                start,
                end,
                uris,
                true);
        return viewStatsList;
    }
}
