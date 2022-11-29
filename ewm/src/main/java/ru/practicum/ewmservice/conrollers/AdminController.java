package ru.practicum.ewmservice.conrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.common.marker.Create;
import ru.practicum.ewmservice.common.marker.Update;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.dto.NewCategoryDto;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.model.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.user.dto.NewUserRequest;
import ru.practicum.ewmservice.model.user.dto.UserDto;
import ru.practicum.ewmservice.service.AdminService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminController {

    private final AdminService adminService;


    //Admin: События

    //Поиск событий
    @GetMapping("/events")
    public List<EventFullDto> findEvent(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Integer> categories,
                                        @RequestParam(required = false) String rangeStart, //Нужна валидация даты
                                        @RequestParam(required = false) String rangeEnd, //Нужна валидация даты
                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("AdminController: GET /admin/events: findEvent() users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd ={}, from ={}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return adminService.findEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    //Редактирование событий
    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer eventId,
                                    @Validated({Update.class}) @RequestBody AdminUpdateEventRequest updateEvent) {
        log.info("AdminController: PUT /admin/events/{}: updateEvent() eventId = {}, updateEvent ={}",
                eventId, eventId, updateEvent);
        return adminService.updateEvent(eventId, updateEvent);
    }

    //Публикация события publish
    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publicationEvent(@Positive @PathVariable Long eventId) {
        log.info("AdminController: PATCH /admin/events/{}/publish: publicationEvent() eventId = {}",
                eventId, eventId);
        return adminService.publicationEvent(eventId);
    }

    //Отклонение события
    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEven(@Positive @PathVariable Long eventId) {
        log.info("AdminController: PATCH /admin/events/{}/reject: rejectEven() eventId = {}",
                eventId, eventId);
        return adminService.rejectEven(eventId);
    }


    //Admin: Категории

    //Изменение категории
    @PatchMapping("/categories")
    public CategoryDto updateCategory(@Validated({Update.class}) @RequestBody CategoryDto categoryDto) {
        log.info("AdminController: PATH /admin/categories: updateCategory() categoryDto = {}", categoryDto);
        return adminService.updateCategory(categoryDto);
    }

    //Добавление новой категории
    @PostMapping("/categories")
    public CategoryDto addCategory(@Validated @RequestBody NewCategoryDto newCategoryDto) {
        log.info("AdminController: POST/admin/categories: addCategory() newСategoryDto = {}", newCategoryDto);
        return adminService.addCategory(newCategoryDto);
    }

    //Удаление категории
    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@Positive @PathVariable Integer catId) {
        log.info("AdminController: DELETE /admin/categories: deleteCategory() catId = {}", catId);
        adminService.deleteCategory(catId);
        return;
    }


    //Admin: Пользователи

    //Получении информации о пользователе
    @GetMapping("/users")
    public List<UserDto> getUser(@RequestParam(required = false) List<Long> ids,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("AdminController: GET /admin/users: getUser() ids = {}, from = {}, size = {}", ids, from, size);
        return adminService.getUser(ids, from, size);
    }

    //Добавление нового пользователя
    @PostMapping("/users")
    public UserDto addUser(@Validated({Create.class}) @RequestBody NewUserRequest newUserRequest) {
        log.info("AdminController: POST /admin/users: addUser() newUserRequest = {}", newUserRequest);
        return adminService.addUser(newUserRequest);
    }

    //Удаление пользователя
    @DeleteMapping("/users/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("AdminController: DELETE /admin/users/{}: deleteUser() userId = {}", userId, userId);
        adminService.deleteUser(userId);
        return;

    }


    //Admin: Подборки событий

    //Добавление новой подборки
    @PostMapping("/compilations")
    public CompilationDto addCompilation(@Validated({Create.class}) @RequestBody NewCompilationDto newCompilationDto) {
        log.info("AdminController: POST /admin/compilations: addCompilation() newCompilationDto = {}", newCompilationDto);
        return adminService.addCompilation(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@Positive @PathVariable Long compId) {
        log.info("AdminController: DELETE /admin/compilations/{}: deleteCompilation() compId = {}", compId, compId);
        adminService.deleteCompilation(compId);
        return;
    }

    //Удалить событие из подборки
    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public CompilationDto deleteEventFromCompilation(@Positive @PathVariable Long compId,
                                                     @Positive @PathVariable Long eventId) {
        log.info("AdminController: DELETE /admin/compilations/{}/events/{}: deleteEventFromCompilation() compId = {}, eventId = {}",
                compId, eventId, compId, eventId);
        return adminService.deleteEventFromCompilation(compId, eventId);
    }

    //Добавить событие в подборку
    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public CompilationDto addEventFromCompilation(@Positive @PathVariable Long compId,
                                                  @Positive @PathVariable Long eventId) {
        log.info("AdminController: PATCH /admin/compilations/{}/events/{}: addEventFromCompilation() compId = {}, eventId = {}",
                compId, eventId, compId, eventId);
        return adminService.addEventFromCompilation(compId, eventId);
    }

    //Открепить подборку на главной странице
    @DeleteMapping("/compilations/{compId}/pin")
    public void deletePinned(@Positive @PathVariable long compId) {
        log.info("AdminController: DELETE /admin/compilations/{}/pin: deletePinned() compId = {}",
                compId, compId);
        adminService.deletePinned(compId);
        return;
    }

    //Закрепить подборку на главной странице
    @PatchMapping("/compilations/{compId}/pin")
    public void addPinned(@Positive @PathVariable long compId) {
        log.info("AdminController: PATCH /admin/compilations/{}/pin: deletePinned() compId = {}",
                compId, compId);
        adminService.addPinned(compId);
        return;
    }
}

