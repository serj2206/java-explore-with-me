package ru.practicum.ewmservice.model.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.model.compilation.Compilation;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;


import java.util.List;

@Component
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events)
                .build();
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtoList) {
        return new CompilationDto(compilation, eventShortDtoList);
    }

}
