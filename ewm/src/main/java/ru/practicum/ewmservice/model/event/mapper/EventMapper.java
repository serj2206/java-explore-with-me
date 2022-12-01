package ru.practicum.ewmservice.model.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.event.dto.*;
import ru.practicum.ewmservice.model.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event, Integer views, Long confirmedRequests) {
        return new EventFullDto(event, views, confirmedRequests);
    }

    public static EventShortDto toEventShortDto(Event event, Integer views, Long confirmedRequests) {
        return new EventShortDto(event, views, confirmedRequests);
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, LocalDateTime createdOn, User initiator) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .paid(newEventDto.getPaid())
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requestModeration(newEventDto.isRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit())
                .createdOn(createdOn)
                .state(State.PENDING)
                .initiator(initiator)
                .build();
    }

    public static Event toUpdateEvent(Category category, AdminUpdateEventRequest updateEventRequest, Event eventDB) {

        if (updateEventRequest.getTitle() != null) {
            eventDB.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getAnnotation() != null) {
            eventDB.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            eventDB.setDescription(updateEventRequest.getDescription());
        }
        if (category != null) {
            eventDB.setCategory(category);
        }
        if (updateEventRequest.getPaid() != null) {
            eventDB.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            eventDB.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getEventDate() != null) {
            eventDB.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return eventDB;
    }

    public static Event toUpdateEvent(Category category, UpdateEventRequest updateEventRequest, Event eventDB) {

        if (updateEventRequest.getTitle() != null) {
            eventDB.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getAnnotation() != null) {
            eventDB.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            eventDB.setDescription(updateEventRequest.getDescription());
        }
        if (category != null) {
            eventDB.setCategory(category);
        }
        if (updateEventRequest.getPaid() != null) {
            eventDB.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            eventDB.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getEventDate() != null) {
            eventDB.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return eventDB;
    }
}
