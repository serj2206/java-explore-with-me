package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.exceptions.BadRequestException;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.model.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.model.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.user.dto.NewUserRequest;
import ru.practicum.ewmservice.model.user.dto.UserDto;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;


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
        return null;
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
    public List<UserDto> getUser(List<Integer> ids, int from, int size) {

        //Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
        // либо о конкретных (учитываются указанные идентификаторы)
        return null;
    }

    //Добавление нового пользователя
    public UserDto addUser(NewUserRequest newUserRequest) {


        //Проверить уникальнось emal
        return null;
    }

    //Удаление пользователя
    public UserDto deleteUser(long userId) {

        return null;
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
