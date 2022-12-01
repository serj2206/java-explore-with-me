package ru.practicum.ewmservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.common.FromSizeRequest;
import ru.practicum.ewmservice.exceptions.ClassErrorException;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.compilation.Compilation;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.compilation.mapper.CompilationMapper;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.QEvent;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventRequestCount;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.request.QRequest;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.statistic.ViewStats;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.CompilationRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PublicService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsService statsService;
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public EventFullDto findEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        Integer views = statsService.getViewsForOneEvent(event, true);
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED);
        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    @Transactional
    public List<EventShortDto> searchEvent(String text,
                                           List<Integer> categories,
                                           Boolean paid,
                                           String rangeStart,
                                           String rangeEnd,
                                           Boolean onlyAvailable,
                                           String sort,
                                           Integer from,
                                           Integer size) {

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        List<BooleanExpression> conditions = new ArrayList<>();
        LocalDateTime start;
        LocalDateTime end;

        if (text != null) {
            conditions.add(QEvent.event.annotation.in(text).or(QEvent.event.description.in(text)));
        }

        if (paid != null) {
            conditions.add(QEvent.event.paid.eq(paid));
        }

        if (categories != null) {
            conditions.add(QEvent.event.category.id.in(categories));
        }
        if (onlyAvailable.equals(true)) {
            conditions.add(QEvent.event.participantLimit
                    .gt(requestRepository.count(QRequest.request.event.eq(QEvent.event)
                            .and(QRequest.request.status.eq(RequestStatus.CONFIRMED)))));
        }

        if (rangeStart != null && rangeEnd == null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            conditions.add(QEvent.event
                    .eventDate.after(start));
        }

        if (rangeStart == null && rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            conditions.add(QEvent
                    .event.eventDate.before(end));
        }

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            conditions.add(QEvent.event.eventDate
                    .between(start, end));
        }

        if (rangeStart == null && rangeEnd == null) {
            start = LocalDateTime.now();
            conditions.add(QEvent.event
                    .eventDate.after(start));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .orElseThrow(() -> new ClassErrorException("Не корректная работа метода сервиса для не зарегистрированных пользователей"));

        Page<Event> eventPage = eventRepository.findAll(finalCondition, pageable);

        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                List<Event> events = eventPage.stream()
                        .sorted((e1, e2) -> {
                            if (e1.getEventDate().isBefore(e2.getEventDate())) return 1;
                            if (e1.getEventDate().equals(e2.getEventDate())) return 0;
                            else return -1;
                        })
                        .collect(Collectors.toList());
                return this.toEventShortDtoList(events);
            }
            if (sort.equals("VIEWS")) {
                List<EventShortDto> eventShortDtoList = this.toEventShortDtoList(eventPage.toList());
                return eventShortDtoList.stream()
                        .sorted(Comparator.comparingInt(EventShortDto::getViews))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    @Transactional
    public List<CompilationDto> searchCompilation(boolean pinned, int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);
        Page<Compilation> compilationPage = compilationRepository.findCompilationByPinned(pinned, pageable);

        return compilationPage
                .stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    public CompilationDto findCompilationById(Long comId) {
        Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NoSuchElementException("Подборка не найдена"));
        return this.toCompilationDto(compilationRepository.save(compilation));
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        List<Event> events = compilation.getEvents();
        return CompilationMapper.toCompilationDto(compilation, this.toEventShortDtoList(events));
    }

    private List<EventShortDto> toEventShortDtoList(List<Event> events) {
        List<EventRequestCount> erc = eventRepository.findEventByEventListRequestConfirm(events);

        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        for (Event event : events) {
            Optional<EventRequestCount> ercOptional = erc
                    .stream()
                    .filter(er -> er.getEvent().getId().equals(event.getId()))
                    .findAny();
            if (ercOptional.isPresent()) {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, null, ercOptional.get().getRequestConfirmCount()));
                erc.remove(ercOptional.get());
            } else {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, null, 0L));
            }
        }
        if (eventShortDtoList.size() == 0) return eventShortDtoList;
        List<ViewStats> viewStatsList = statsService.getViewsStatsListByEventShort(eventShortDtoList);

        for (EventShortDto e : eventShortDtoList) {
            Optional<ViewStats> viewStatsOptional = viewStatsList.stream()
                    .filter(v -> v.getUri().contains(e.getId().toString()))
                    .findAny();
            if (viewStatsOptional.isPresent()) {
                e.setViews(viewStatsOptional.get().getHits());
                viewStatsList.remove(viewStatsOptional.get());
            } else e.setViews(0);
        }
        return eventShortDtoList;
    }

    public List<CategoryDto> getCategories(int from, int size) {

        List<CategoryDto> categoryDtoList;
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        Page<Category> categoryPage = categoryRepository.findCategoriesByIdIsNotNull(pageable);

        categoryDtoList = categoryPage.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        return categoryDtoList;
    }

    public CategoryDto findCategoryById(Integer catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId).orElseThrow());
    }
}
