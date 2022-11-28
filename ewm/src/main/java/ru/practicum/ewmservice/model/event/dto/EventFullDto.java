package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.location.Location;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

import java.time.format.DateTimeFormatter;

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

    private CategoryDto category;

    private UserShortDto initiator;

    private Location location;

    private State state;

    private Boolean paid;

    private Long participantLimit;

    private String publishedOn;

    private String createdOn;

    //Количество просмотров
    private Integer views;

    //Количество оформленных заявок
    private Long confirmedRequests;

    private Boolean requestModeration;

    private String eventDate;

    public EventFullDto(Event event, Integer views, Long confirmedRequests) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.annotation = event.getAnnotation();
        this.description = event.getDescription();
        this.category = new CategoryDto(event.getCategory());
        this.initiator = new UserShortDto(event.getInitiator());
        this.location = new Location(event.getLat(), event.getLon());
        this.state = event.getState();
        this.paid = event.getPaid();
        this.participantLimit = event.getParticipantLimit();
        if (event.getPublishedOn() != null) {
            this.publishedOn = event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (event.getCreatedOn() != null) {
            this.createdOn = event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        this.views = views;
        this.confirmedRequests = confirmedRequests;
        this.requestModeration = event.getRequestModeration();
        if (event.getEventDate() != null) {
            this.eventDate = event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
