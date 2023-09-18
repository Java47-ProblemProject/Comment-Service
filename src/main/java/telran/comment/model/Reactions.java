package telran.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Reactions {
    protected Integer totalLikes;
    protected Integer totalDislikes;
    protected Set<ProfileDetails> likes;
    protected Set<ProfileDetails> dislikes;

    public Reactions() {
        this.totalLikes = 0;
        this.totalDislikes = 0;
        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
        ;
    }

    public boolean setLike(String profileId, Double profileRating) {
        boolean isLiked = this.likes.stream().anyMatch(profileDetails -> profileDetails.getProfileId().equals(profileId));
        if (isLiked) {
            ProfileDetails profileToRemove = this.likes.stream()
                    .filter(profileDetails -> profileDetails.getProfileId().equals(profileId))
                    .findFirst()
                    .get();
            this.likes.remove(profileToRemove);
            this.totalLikes--;
        } else {
            ProfileDetails newLike = new ProfileDetails(profileId, profileRating);
            this.likes.add(newLike);
            this.totalLikes++;
        }
        return !isLiked;
    }

    public boolean setDislike(String profileId, Double profileRating) {
        boolean isDisliked = this.dislikes.stream().anyMatch(profileDetails -> profileDetails.getProfileId().equals(profileId));
        if (isDisliked) {
            ProfileDetails profileToRemove = this.dislikes.stream()
                    .filter(profileDetails -> profileDetails.getProfileId().equals(profileId))
                    .findFirst()
                    .get();
            this.dislikes.remove(profileToRemove);
            this.totalDislikes--;
        } else {
            ProfileDetails newDislike = new ProfileDetails(profileId, profileRating);
            this.dislikes.add(newDislike);
            this.totalDislikes++;
        }
        return !isDisliked;
    }
}
