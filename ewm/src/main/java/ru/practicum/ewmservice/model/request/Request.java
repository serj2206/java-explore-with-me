package ru.practicum.ewmservice.model.request;

import lombok.*;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @CollectionTable(name = "events",  joinColumns = @JoinColumn(name = "id"))
    private Event event;

    @ManyToOne
    @CollectionTable(name = "users",  joinColumns = @JoinColumn(name = "id"))
    private User requester;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
