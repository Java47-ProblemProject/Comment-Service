package telran.comment.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Getter
@EqualsAndHashCode(of = "profileId")
public class ProfileDetails {
    String profileId;
    Double profileRating;
    LocalDateTime dateCreated;

    public ProfileDetails(String profileId, Double profileRating) {
        this.profileId = profileId;
        this.profileRating = profileRating;
        ZoneId jerusalemZone = ZoneId.of("Asia/Jerusalem");
        this.dateCreated = LocalDateTime.now(ZoneOffset.UTC).atZone(jerusalemZone).toLocalDateTime();
    }
}
