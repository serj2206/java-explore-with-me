package ru.practicum.ewmservice.model.compilation;

import lombok.*;
import ru.practicum.ewmservice.model.event.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compilations")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "pinned")
    private boolean pinned;

    @Column(name = "event_id")
    @ManyToMany
    @JoinColumn(name = "id")
    private List<Event> events = new ArrayList<>();

}
