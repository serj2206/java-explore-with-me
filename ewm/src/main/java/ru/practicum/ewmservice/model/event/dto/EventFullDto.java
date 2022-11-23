package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.location.Location;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private Long id;

    private String title;

    private String annotation;

    private String description;

    private CategoryDto categoryDto;

    private UserShortDto initiator;

    private Location location;

    private State state;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private LocalDateTime createdOn;

    //Количество просмотров
    private Integer views;

    //Количество оформленных заявок
    private Integer confirmedRequests;

    private Boolean requestModeration;

    private LocalDateTime eventDate;

    public EventFullDto(Event event, Integer views, Integer confirmedRequests) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.annotation = event.getAnnotation();
        this.description = event.getDescription();
        this.categoryDto = new CategoryDto(event.getCategory());
        this.initiator = new UserShortDto(event.getInitiator());
        this.location = new Location(event.getLat(), event.getLon());
        this.state = event.getState();
        this.paid = event.isPaid();
        this.participantLimit = event.getParticipantLimit();
        this.publishedOn = event.getPublishedOn();
        this.createdOn = event.getCreatedOn();
        this.views = views;
        this.confirmedRequests = confirmedRequests;
        this.requestModeration = event.isRequestModeration();
        this.eventDate = event.getEventDate();
    }
}