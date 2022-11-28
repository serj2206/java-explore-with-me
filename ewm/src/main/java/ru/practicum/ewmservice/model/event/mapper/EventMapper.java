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

    public static EventShortDto toEventShortDto(EventRequestCount erc, Integer views) {
        return new EventShortDto(erc.getEvent(), views, erc.getRequestConfirmCount());
    }

    public static EventShortDto toEventShortDto(Event event, Integer views, Long confirmedRequests) {
        return new EventShortDto(event, views, confirmedRequests);
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, LocalDateTime createdOn, User initiator) {

        Event event = Event.builder()
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
        return event;
    }

    public static Event toUpdateEvent(Long eventId, Category category, AdminUpdateEventRequest updateEventRequest, Event eventDB) {
        Event event = eventDB;

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return event;
    }

    public static Event toUpdateEvent(Category category, UpdateEventRequest updateEventRequest, Event eventDB) {
        Event event = eventDB;

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (category != null) {
            event.setCategory(category);
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return event;
    }


}
