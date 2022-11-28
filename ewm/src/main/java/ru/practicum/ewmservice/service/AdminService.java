package ru.practicum.ewmservice.service;


import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.common.FromSizeRequest;

import ru.practicum.ewmservice.exceptions.BadRequestException;
import ru.practicum.ewmservice.exceptions.ViolationRuleException;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.model.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.compilation.Compilation;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.model.compilation.mapper.CompilationMapper;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.QEvent;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventRequestCount;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.statistic.ViewStats;
import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.model.user.dto.NewUserRequest;
import ru.practicum.ewmservice.model.user.dto.UserDto;
import ru.practicum.ewmservice.model.user.mapper.UserMapper;
import ru.practicum.ewmservice.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsService statsService;
    private final CompilationRepository compilationRepository;

    //Поиск событий
    @Transactional
    public List<EventFullDto> findEvent(List<Long> users,
                                        List<String> states,
                                        List<Integer> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size) {


        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        List<State> stateList = new ArrayList<>();
        if (states != null) {
            stateList = states.stream()
                    .map(s -> State.from(s).orElseThrow(() -> new BadRequestException("Unknown state: " + s)))
                    .collect(Collectors.toList());
        }

        List<BooleanExpression> conditions = new ArrayList<>();
        LocalDateTime start;
        LocalDateTime end;

        if (users != null) {
            conditions.add(QEvent.event.initiator.id.in(users));
        }

        if (states != null) {
            conditions.add(QEvent.event.state.in(stateList));
        }

        if (categories != null) {
            conditions.add(QEvent.event.category.id.in(categories));
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

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Page<Event> eventPage = eventRepository.findAll(finalCondition, pageable);
        List<Event> events = eventPage.toList();
        return this.toEventFullDtoList(events);
    }

    private List<EventFullDto> toEventFullDtoList(List<Event> events) {
        List<EventRequestCount> erc = eventRepository.findEventByEventListRequestConfirm(events);

        List<EventFullDto> eventFullDtoList = new ArrayList<>();

        for (Event event : events) {
            Optional<EventRequestCount> ercOptional = erc
                    .stream()
                    .filter(er -> er.getEvent().getId() == event.getId())
                    .findAny();
            if (ercOptional.isPresent()) {
                eventFullDtoList.add(EventMapper.toEventFullDto(event, null, ercOptional.get().getRequestConfirmCount()));
                erc.remove(ercOptional.get());
            } else {
                eventFullDtoList.add(EventMapper.toEventFullDto(event, null, 0L));
            }
        }

        if (eventFullDtoList.size() == 0) return eventFullDtoList;
        List<ViewStats> viewStatsList = statsService.getViewsStatsListByEventFull(eventFullDtoList);

        for (EventFullDto e : eventFullDtoList) {
            Optional<ViewStats> viewStatsOptional = viewStatsList.stream()
                    .filter(v -> v.getUri().contains(e.getId().toString()))
                    .findAny();
            if (viewStatsOptional.isPresent()) {
                e.setViews(viewStatsOptional.get().getHits());
                viewStatsList.remove(viewStatsOptional.get());
            } else e.setViews(0);
        }
        return eventFullDtoList;
    }


    @Transactional
    //Редактирование событий
    public EventFullDto updateEvent(long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {

        Category category = categoryRepository.findById(adminUpdateEventRequest.getCategory()).orElseThrow();
        Event eventDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Событие не найдено")));

        Event updateEvent = EventMapper.toUpdateEvent(eventId, category, adminUpdateEventRequest, eventDb);
        Event event = eventRepository.save(updateEvent);

        Integer views = statsService.getViewsForOneEvent(event, true);

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    //Публикация события
    @Transactional
    public EventFullDto publicationEvent(long eventId) {
        /*
        Дата начала события должна быть не ранее чем за час от даты публикации.
        Событие должно быть в состоянии ожидания публикации
         */
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        if (LocalDateTime.now().isAfter(event.getEventDate().minusHours(1))) {
            throw new ViolationRuleException(
                    String.format("Публикация события с id = %d невозможна, событие наступит менее через час", eventId));
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        Event eventNew = eventRepository.save(event);
        Integer views = statsService.getViewsForOneEvent(eventNew, true);
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        return EventMapper.toEventFullDto(eventNew, views, confirmedRequests);
    }

    //Отклонение события
    @Transactional
    public EventFullDto rejectEven(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        //Обратите внимание: событие не должно быть опубликовано.
        if (event.getState() == State.PUBLISHED) {
            throw new ViolationRuleException(
                    String.format("Отклонение события с id = %d невозможно, событие опубликовано", eventId));
        }
        event.setState(State.CANCELED);
        Integer views = statsService.getViewsForOneEvent(event, true);
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        Event eventNew = eventRepository.save(event);

        return EventMapper.toEventFullDto(eventNew, views, confirmedRequests);
    }

    //Admin: Категории

    //Изменение категории
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        Category categoryDb = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Категория не найдена"));
        if (category.getName() != null) {
            categoryDb.setName(category.getName());
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    //Добавление новой категории
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    //Удаление категории
    @Transactional
    public void deleteCategory(int catId) {
        categoryRepository.deleteById(catId);
        return;
    }

    //Admin: Пользователи

    //Получение информации о пользователе
    @Transactional(readOnly = true)
    public List<UserDto> getUser(List<Long> ids, int from, int size) {

        List<UserDto> userDtoList;
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        Page<User> usersPage = userRepository.findUsersById(ids, pageable);

        userDtoList = usersPage.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        return userDtoList;
    }

    //Добавление нового пользователя
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    //Удаление пользователя
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        return;
    }


    //Admin: Подборки событий

    //Добавление новой подборки
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilationNew = compilationRepository
                .save(CompilationMapper.toCompilation(newCompilationDto, events));

        return this.toCompilationDto(compilationNew);
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        List<Event> events = compilation.getEvents();
        List<EventRequestCount> erc = eventRepository.findEventByEventListRequestConfirm(events);

        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        for (Event event : events) {
            Optional<EventRequestCount> ercOptional = erc
                    .stream()
                    .filter(er -> er.getEvent().getId() == event.getId())
                    .findAny();
            if (ercOptional.isPresent()) {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, null, ercOptional.get().getRequestConfirmCount()));
                erc.remove(ercOptional.get());
            } else {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, null, 0L));
            }
        }
        return CompilationMapper.toCompilationDto(compilation, this.toEventShortDtoList(eventShortDtoList));
    }

    private List<EventShortDto> toEventShortDtoList(List<EventShortDto> eventShortDtoList) {
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


    //Удаление подборки
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
        return;
    }

    //Удалить событие из подборки
    @Transactional
    public CompilationDto deleteEventFromCompilation(long comId, long eventId) {
        Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NoSuchElementException("Подборка не найдена"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        compilation.getEvents().remove(event);
        return this.toCompilationDto(compilationRepository.save(compilation));
    }

    //Добавить событие в подборку
    @Transactional
    public CompilationDto addEventFromCompilation(long comId, long eventId) {
        Compilation compilation = compilationRepository.findById(comId)
                .orElseThrow(() -> new NoSuchElementException("Подборка не найдена"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        compilation.getEvents().add(event);
        return this.toCompilationDto(compilationRepository.save(compilation));
    }

    //Открепить подборку на главной странице
    @Transactional
    public void deletePinned(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElementException("Подборка не найдена"));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        return;
    }

    //Закрепить подборку на главной странице
    @Transactional
    public void addPinned(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElementException("Подборка не найдена"));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        return;
    }
}
