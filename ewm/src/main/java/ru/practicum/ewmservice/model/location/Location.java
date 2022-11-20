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
    private float lat;

    //Долгота
    private float lon;

    public Location(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
