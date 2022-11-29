package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    private Long id;

    private String title;

    private String annotation;

    private String description;

    private CategoryDto category;

    private UserShortDto initiator;

    private boolean paid;

    //Количество просмотров
    private Integer views;

    //Количество оформленных заявок
    private Long confirmedRequests;

    private String eventDate;

    public EventShortDto(Event event, Integer views, Long confirmedRequests) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.annotation = event.getAnnotation();
        this.description = event.getDescription();
        this.category = new CategoryDto(event.getCategory());
        this.initiator = new UserShortDto(event.getInitiator());
        this.paid = event.getPaid();
        this.views = views;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public EventShortDto(Event event, Long confirmedRequests) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.annotation = event.getAnnotation();
        this.description = event.getDescription();
        this.category = new CategoryDto(event.getCategory());
        this.initiator = new UserShortDto(event.getInitiator());
        this.paid = event.getPaid();
        this.views = null;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));;
    }






}
