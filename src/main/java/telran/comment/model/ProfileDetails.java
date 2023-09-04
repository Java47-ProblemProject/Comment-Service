package telran.comment.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "profileId")
public class ProfileDetails {
    String profileId;
    Double profileRating;
    LocalDateTime dateCreated;
    public ProfileDetails(String profileId, Double profileRating) {
        this.profileId = profileId;
        this.profileRating = profileRating;
        this.dateCreated = LocalDateTime.now();
    }
}
