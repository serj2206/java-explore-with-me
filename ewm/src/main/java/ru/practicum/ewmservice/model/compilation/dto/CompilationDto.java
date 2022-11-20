package ru.practicum.ewmservice.model.compilation.dto;


import lombok.*;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;

import java.util.Set;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    Set<EventShortDto> events;
    long id;
    boolean pinned;
    String title;
}
