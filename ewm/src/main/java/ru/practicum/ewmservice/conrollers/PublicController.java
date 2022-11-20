package ru.practicum.ewmservice.conrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.model.categorie.dto.CategoryDto;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.service.PublicService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {
    private final PublicService publicService;


    //Public: События

    //Получение подробной информации о опубликованном событии
    @GetMapping("/events/{id}")
    public EventFullDto findEventById(@Positive(message = "ID меньше нуля") @PathVariable Long id) {
        return publicService.findEventById(id);
    }

    //Получение событий с возможностью фильтрации

    //text - Текст для поиска в содержимом аннотации и подробном описании события
    //categories - Список идентификаторов категорий в которых будет вестись поиск
    //paid - Поиск только платных/бесплатных событий
    //rangeStart - Дата и время не раньше которых должно произойти событие
    //rangeEnd - Дата и время не позже которых должно произойти событие
    //onlyAvailable - Только события у которых не исчерпан лимит запросов на участие
    //sort - Вариант сортировки: по дате события или по количеству просмотров
    //from - Количество событий, которые нужно пропустить для формирования текущего набора
    //size - Количество событий в наборе

    @GetMapping("/events")
    public List<EventShortDto> searchEvent(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Integer> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false) String rangeStart, //Нужна валидация даты
                                           @RequestParam(required = false) String rangeEnd, //Нужна валидация даты
                                           @RequestParam(required = false) boolean onlyAvailable,
                                           @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        return publicService.searchEvent(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }


    //Public: Подборки событий

    //Получение подборок событий
    @GetMapping("/compilations")
    public CompilationDto searchCompilation(@RequestParam boolean pinned,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return publicService.searchCompilation(pinned, from, size);
    }

    //Получение подборки событий по её ID
    @GetMapping("/compilations/{comId}")
    public CompilationDto findCompilationById(@Positive @PathVariable Integer comId) {
        return publicService.findCompilationById(comId);
    }


    //Public: Категории

    //Получение категорий
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return publicService.getCategories(from, size);
    }

    //Получение информации категории по её ID
    @GetMapping("/categories/{catId}")
    public CategoryDto findCategoryById(@Positive @PathVariable Integer catId) {
        return publicService.findCategoryById(catId);
    }
}
