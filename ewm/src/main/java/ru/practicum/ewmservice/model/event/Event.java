package ru.practicum.ewmservice.model.event;


import lombok.*;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.compilation.Compilation;
import ru.practicum.ewmservice.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Название
    @Column(name = "title")
    private String title;

    //Краткое описание
    @Column(name = "annotation")
    private String annotation;

    //Полное описание события
    @Column(name = "description")
    private String description;

    //Категория события
    @ManyToOne
    @CollectionTable(name = "categorys", joinColumns = @JoinColumn(name = "id"))
    private Category category;

    @ManyToOne
    @CollectionTable(name = "users",  joinColumns = @JoinColumn(name = "id"))
    private User initiator;

    //Широта
    @Column(name = "lat")
    private float lat;

    //Долгота
    @Column(name = "lon")
    private float lon;

    //Cостояние жизненного цикла события
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    //Нужно ли оплачивать участие
    @Column(name = "paid")
    private boolean paid;

    //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    @Column(name = "participant_limit")
    private int participantLimit;

    //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    //Подборки
    @ManyToMany
    @JoinTable(name = "compilation_id")
    private List<Compilation> compilationList = new ArrayList<>();

    //Пре-модерация заявок на участие
    @Column(name = "request_moderation")
    private boolean requestModeration;

    //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "event_date")
    private LocalDateTime eventDate;


}
