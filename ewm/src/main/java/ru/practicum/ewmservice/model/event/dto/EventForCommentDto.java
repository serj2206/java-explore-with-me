package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

import java.time.format.DateTimeFormatter;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventForCommentDto {
    private Long id;

    private String title;

    private UserShortDto initiator;

    private String eventDate;

    public EventForCommentDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.initiator = new UserShortDto(event.getInitiator());
        this.eventDate = event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
