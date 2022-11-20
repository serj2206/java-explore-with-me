package ru.practicum.ewmservice.model.compilation;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "events")
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
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "pinned")
    private boolean pinned;
}
