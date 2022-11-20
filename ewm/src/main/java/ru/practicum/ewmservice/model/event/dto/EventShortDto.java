package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.categorie.Category;
import ru.practicum.ewmservice.model.categorie.dto.CategoryDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

import java.time.LocalDateTime;

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

    private CategoryDto categoryDto;

    private UserShortDto initiator;

    private boolean paid;

    //Количество просмотров
    private int views;

    //Количество оформленных заявок
    private int confirmedRequests;

    private LocalDateTime eventDate;

    public EventShortDto(Event event, int views, int confirmedRequests) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.annotation = event.getAnnotation();
        this.description = event.getDescription();
        this.categoryDto = new CategoryDto(event.getCategory());
        this.initiator = new UserShortDto(event.getInitiator());
        this.paid = event.isPaid();
        this.views = views;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = event.getEventDate();
    }
}
