package telran.comment.dto.accounting;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public class ProfileDto {
    protected String username;
    protected String email;
    protected String educationLevel;
    protected Set<String> communities;
    protected LocationDto location;
    protected Set<String> roles;
    protected String avatar;
    protected StatsDto stats;
    protected Map<String, ActivityDto> activities;
    protected Double wallet;
}
