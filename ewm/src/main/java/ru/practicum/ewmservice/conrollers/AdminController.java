package ru.practicum.ewmservice.conrollers;

import lombok.RequiredArgsConstructor;
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
        return adminService.findEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    //Редактирование событий
    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Integer eventId,
                                    @Validated({Update.class}) @RequestBody AdminUpdateEventRequest updateEvent) {
        return adminService.updateEvent(eventId, updateEvent);
    }

    //Публикация события
    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publicationEvent(@Positive @PathVariable Long eventId) {
        return adminService.publicationEvent(eventId);
    }

    //Отклонение события
    @PatchMapping("/admin/events/{eventId}/reject")
    public EventFullDto rejectEven(@Positive @PathVariable Long eventId) {
        return adminService.rejectEven(eventId);
    }


    //Admin: Категории

    //Изменение категории
    @PatchMapping("/categories")
    public CategoryDto updateCategory(@Validated({Update.class}) @RequestBody CategoryDto categoryDto) {
        return adminService.updateCategory(categoryDto);
    }

    //Добавление новой категории
    @PostMapping("/categories")
    public CategoryDto addCategory(@Validated({Create.class}) @RequestBody NewCategoryDto newСategoryDto) {
        return adminService.addCategory(newСategoryDto);
    }

    //Удаление категории
    @DeleteMapping("/categories/{catId}")
    public CategoryDto deleteCategory(@Positive @PathVariable Integer catId) {
        return adminService.deleteCategory(catId);
    }


    //Admin: Пользователи

    //Получении информации о пользователе
    @GetMapping("/users")
    public List<UserDto> getUser(@RequestParam(required = false) List<Integer> ids,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                 @Positive @RequestParam(defaultValue = "10") int size) {
        return adminService.getUser(ids, from, size);
    }

    //Добавление нового пользователя
    @PostMapping("/users")
    public UserDto addUser(@Validated({Create.class}) @RequestBody NewUserRequest newUserRequest) {
        return adminService.addUser(newUserRequest);
    }

    //Удаление пользователя
    @DeleteMapping("/users/{userId}")
    public UserDto deleteUser(@Positive @PathVariable Long userId) {
        return adminService.deleteUser(userId);
    }


    //Admin: Подборки событий

    //Добавление новой подборки
    @PostMapping("/compilations")
    public CompilationDto addCompilation(@Validated({Create.class}) @RequestBody NewCompilationDto newCompilationDto) {
        return adminService.addCompilation(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@Positive @PathVariable Long compId) {
        adminService.deleteCompilation(compId);
        return;
    }

    //Удалить событие из подборки
    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public CompilationDto deleteEventFromCompilation(@Positive @PathVariable Long comId,
                                                     @Positive @PathVariable Long eventId) {
        return adminService.deleteEventFromCompilation(comId, eventId);
    }

    //Добавить событие в подборку
    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public CompilationDto addEventFromCompilation(@Positive @PathVariable Long comId,
                                                  @Positive @PathVariable Long eventId) {
        return adminService.addEventFromCompilation(comId, eventId);
    }

    //Открепить подборку на главной странице
    @DeleteMapping("/compilations/{compId}/pin")
    public void deletePinned(@Positive @PathVariable long compId) {
        adminService.deletePinned(compId);
        return;
    }

    //Закрепить подборку на главной странице
    @PatchMapping("/compilations/{compId}/pin")
    public void addPinned(@Positive @PathVariable long compId) {
        adminService.addPinned(compId);
        return;
    }
}

/*
План
1. AdminController - V
2. Client - не понятно
3. StatController
4. StatServer
5. StatRepository
6. Исключения
7.
 */