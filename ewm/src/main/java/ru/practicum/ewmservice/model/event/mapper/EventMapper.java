package ru.practicum.ewmservice.model.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;

@Component
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event, int views, int confirmedRequests){
        return new EventFullDto(event, views, confirmedRequests);
    }
}
