package ru.practicum.ewmservice.model.location;

import lombok.*;

import javax.persistence.*;


@EqualsAndHashCode
@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Location {

    //Широта
    private Float lat;

    //Долгота
    private Float lon;

    public Location(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
