package telran.comment.kafka.profileDataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
@ToString
public class ProfileDataDto {
    @Setter
    private String token;
    @Setter
    private String userName;
    private final String email;
    private Double rating;
    private Set<String> communities;
    private Map<String, Activity> activities;
    private final ProfileMethodName methodName;
}
