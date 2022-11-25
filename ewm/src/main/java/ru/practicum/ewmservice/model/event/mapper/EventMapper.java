package ru.practicum.ewmservice.model.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.event.dto.*;
import ru.practicum.ewmservice.model.user.User;

import java.time.LocalDateTime;

@Component
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event, Integer views, Integer confirmedRequests){
        return new EventFullDto(event, views, confirmedRequests);
    }

    public static EventShortDto eventShortDto(EventRequestCount erc, int views) {
        return new EventShortDto(erc.getEvent(), views, erc.getRequestConfirmCount());
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, LocalDateTime createdOn, User initiator) {

        Event event = Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .paid(newEventDto.getPaid())
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLat())
                .eventDate(newEventDto.getEventDate())
                .requestModeration(newEventDto.getRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit())
                .createdOn(createdOn)
                .state(State.PENDING)
                .initiator(initiator)
                .build();
        return event;
    }

    public static Event toUpdateEvent(Long eventId, Category category, AdminUpdateEventRequest updateEventRequest) {

        Event event = Event.builder()
                .id(eventId)
                .title(updateEventRequest.getTitle())
                .annotation(updateEventRequest.getAnnotation())
                .description(updateEventRequest.getDescription())
                .category(category)
                .paid(updateEventRequest.getPaid())
                .participantLimit(updateEventRequest.getParticipantLimit())
                .eventDate(updateEventRequest.getEventDate())
                .build();
        return event;
    }

    public static Event toUpdateEvent(Category category, UpdateEventRequest updateEventRequest) {
        Event event = Event.builder()
                .id(updateEventRequest.getEventId())
                .title(updateEventRequest.getTitle())
                .annotation(updateEventRequest.getAnnotation())
                .description(updateEventRequest.getDescription())
                .category(category)
                .paid(updateEventRequest.getPaid())
                .participantLimit(updateEventRequest.getParticipantLimit())
                .eventDate(updateEventRequest.getEventDate())
                .build();
        return event;
    }




}
