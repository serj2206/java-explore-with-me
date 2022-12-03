package ru.practicum.ewmservice.model.event;

import lombok.*;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Float lat;

    //Долгота
    @Column(name = "lon")
    private Float lon;

    //Cостояние жизненного цикла события
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    //Нужно ли оплачивать участие
    @Column(name = "paid")
    private Boolean paid;

    //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    @Column(name = "participant_limit")
    private Long participantLimit;

    //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    //Пре-модерация заявок на участие
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "event_date")
    private LocalDateTime eventDate;

}
