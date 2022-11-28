package ru.practicum.ewmservice.model.compilation.dto;


import lombok.*;
import ru.practicum.ewmservice.model.compilation.Compilation;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;

import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {
    List<EventShortDto> events;
    Long id;
    boolean pinned;
    String title;

    public CompilationDto(Compilation compilation, List<EventShortDto> eventShortDtoList) {
        this.events = eventShortDtoList;
        this.id = compilation.getId();
        this.pinned = compilation.isPinned();
        this.title = compilation.getTitle();
    }
}
