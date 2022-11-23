package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.client.PublicClient;
import ru.practicum.ewmservice.common.FromSizeRequest;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.model.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.statistic.ViewStats;
import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.model.user.dto.NewUserRequest;
import ru.practicum.ewmservice.model.user.dto.UserDto;
import ru.practicum.ewmservice.model.user.mapper.UserMapper;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final PublicClient publicClient;


    //Поиск событий
    public List<EventFullDto> findEvent(List<Long> users,
                                        List<String> states,
                                        List<Integer> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size) {
        return null;
    }

    //Редактирование событий
    public EventFullDto updateEvent(long eventId, AdminUpdateEventRequest updateEvent) {

        Category category = categoryRepository.findById(updateEvent.getCategory()).orElseThrow();

        Event event = EventMapper.toUpdateEvent(eventId, category, updateEvent);
        Event eventUpdate = eventRepository.save(event);

        LocalDateTime start = eventUpdate.getPublishedOn();
        LocalDateTime end = eventUpdate.getEventDate();
        List<String> uris = List.of("");
        List<ViewStats> viewStatsList = (List<ViewStats>) publicClient.findStats(
                start.toString(),
                end.toString(),
                uris,
                true).getBody();
        Integer views = viewStatsList.get(0).getHits();
        Integer confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRM);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    //Публикация события
    public EventFullDto publicationEvent(long eventId) {
        /*
        Дата начала события должна быть не ранее чем за час от даты публикации.
        Событие должно быть в состоянии ожидания публикации
         */
        return null;
    }

    //Отклонение события
    public EventFullDto rejectEven(long eventId) {
        //Обратите внимание: событие не должно быть опубликовано.
        return null;
    }

    //Admin: Категории

    //Изменение категории
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);

        //Обратите внимание: имя категории должно быть уникальным
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    //Добавление новой категории
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    //Удаление категории
    public void deleteCategory(int catId) {
        categoryRepository.deleteById(catId);

        //Обратите внимание: с категорией не должно быть связано ни одного события.
        return;
    }

    //Admin: Пользователи

    //Получении информации о пользователе
    public List<UserDto> getUser(List<Long> ids, int from, int size) {

        List<UserDto> userDtoList;
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        Page<User> usersPage = userRepository.findUsersById(ids, pageable);

        userDtoList = usersPage.stream()
                .map(UserMapper :: toUserDto)
                .collect(Collectors.toList());

        return userDtoList;
    }

    //Добавление нового пользователя
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    //Удаление пользователя
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        return;
    }


    //Admin: Подборки событий

    //Добавление новой подборки
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {

        return null;
    }

    //Удаление подборки
    public void deleteCompilation(long compId) {

        return;
    }

    //Удалить событие из подборки
    public CompilationDto deleteEventFromCompilation(long comId, long eventId) {

        return null;
    }

    //Добавить событие из подборки
    public CompilationDto addEventFromCompilation(long comId, long eventId) {

        return null;
    }

    //Открепить подборку на главной странице
    public void deletePinned(long compId) {

        return;
    }

    //Закрепить подборку на главной странице
    public void addPinned(long compId) {

        return;
    }

}
