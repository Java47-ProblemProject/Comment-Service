package telran.comment.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class ProfileDto {
    protected String username;
    protected String email;
    protected String educationLevel;
    protected Set<String> communities;
    protected LocationDto location;
    protected String password;
    protected Set<String> roles;
    protected String avatar;
    protected StatsDto stats;
    protected Map<String, ActivityDto> activities;
    protected Double wallet;

    public void addActivity(String id, ActivityDto activity) {
        this.activities.put(id, activity);
    }

    public void removeActivity(String id) {
        this.activities.remove(id);
    }
}
