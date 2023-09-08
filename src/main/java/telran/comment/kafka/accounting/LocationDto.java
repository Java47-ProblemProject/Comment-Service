package telran.comment.kafka.accounting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor

@Getter
public class LocationDto {
    protected String country;
    protected String city;
}