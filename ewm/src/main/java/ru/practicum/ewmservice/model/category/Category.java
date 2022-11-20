package ru.practicum.ewmservice.model.category;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;
}
