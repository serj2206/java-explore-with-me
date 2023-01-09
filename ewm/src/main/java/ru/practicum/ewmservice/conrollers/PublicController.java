package ru.practicum.ewmservice.conrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.client.PublicClient;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.statistic.EndpointHitDto;
import ru.practicum.ewmservice.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {
    private final PublicService publicService;
    private final PublicClient publicClient;


    //Public: События

    //Получение подробной информации о опубликованном событии
    @GetMapping("/events/{id}")
    public EventFullDto findEventById(@Positive(message = "ID меньше единицы") @PathVariable Long id,
                                      HttpServletRequest request) {
        log.info("PublicController: GET /events/{}: findEventById() id = {}", id, id);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String api = "ewm-main-service";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        publicClient.addStats(new EndpointHitDto(api, uri, ip, timestamp));
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
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(required = false) boolean onlyAvailable,
                                           @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        log.info("AdminController: GET /events: searchEvent() text = {}, categories = {}, paid = {}, rangeStart = {}, " +
                        "rangeEnd ={}, onlyAvailable={}, sort ={}, from ={}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String api = "ewm-main-service";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        publicClient.addStats(new EndpointHitDto(api, uri, ip, timestamp));
        return publicService.searchEvent(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }


    //Public: Подборки событий

    //Получение подборок событий
    @GetMapping("/compilations")
    public List<CompilationDto> searchCompilation(@RequestParam(defaultValue = "false") boolean pinned,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicController: GET /compilations: searchCompilation() pinned = {}, from = {}, size = {}",
                pinned, from, size);
        return publicService.searchCompilation(pinned, from, size);
    }

    //Получение подборки событий по её ID
    @GetMapping("/compilations/{comId}")
    public CompilationDto findCompilationById(@Positive @PathVariable Long comId) {
        log.info("PublicController: GET /compilations/{}: searchCompilation() comId = {}", comId, comId);
        return publicService.findCompilationById(comId);
    }


    //Public: Категории

    //Получение категорий
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicController: GET /categories: getCategories() from = {}, size = {}",
                from, size);
        return publicService.getCategories(from, size);
    }

    //Получение информации категории по её ID
    @GetMapping("/categories/{catId}")
    public CategoryDto findCategoryById(@Positive @PathVariable Integer catId) {
        log.info("PublicController: GET /categories/{}: findCategoryById() catId = {}", catId, catId);
        return publicService.findCategoryById(catId);
    }
}
