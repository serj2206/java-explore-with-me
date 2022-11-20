package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.common.FromSizeRequest;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.model.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PublicService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    public EventFullDto findEventById(Long id) {
        int views = 0; //Получить количество просмотров из сервера статистики
        int confirmedRequests = 0; //Узнать количество оформленны заявок
        Event event = eventRepository.findById(id).orElseThrow();
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event, views, confirmedRequests);
        //Отправить статистику в сервер статистики
        return eventFullDto;
    }

    public List<EventShortDto> searchEvent(String text,
                                           List<Integer> categories,
                                           Boolean paid,
                                           String rangeStart,
                                           String rangeEnd,
                                           Boolean onlyAvailable,
                                           String sort,
                                           Integer from,
                                           Integer size) {

        return null;
    }

    public CompilationDto searchCompilation(boolean pinned, int from, int size) {
        return null;
    }

    public CompilationDto findCompilationById(int id) {
        return null;
    }

    public List<CategoryDto> getCategories(int from, int size) {

        List<CategoryDto> categoryDtoList;
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        Page<Category> categoryPage = categoryRepository.findCategoriesByIdIsNotNull(pageable);

        categoryDtoList = categoryPage.stream()
                .map(CategoryMapper :: toCategoryDto)
                .collect(Collectors.toList());

        return categoryDtoList;
    }

    public CategoryDto findCategoryById(Integer catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId).orElseThrow());
    }
}
