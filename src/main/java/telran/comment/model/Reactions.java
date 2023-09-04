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
        this.dislikes = new HashSet<>();;
    }

    public boolean setLike(String profileId, Double profileRating) {
        ProfileDetails profileDetails = new ProfileDetails(profileId, profileRating);
        if (this.likes.contains(profileDetails)) {
            this.likes.remove(profileDetails);
            this.totalLikes--;
            return false;
        } else {
            if (this.dislikes.contains(profileDetails)) {
                this.dislikes.remove(profileDetails);
                this.totalDislikes--;
            }
            this.likes.add(profileDetails);
            this.totalLikes++;
            return true;
        }
    }

    public boolean setDislike(String profileId, Double profileRating) {
        ProfileDetails profileDetails = new ProfileDetails(profileId, profileRating);
        if (this.dislikes.contains(profileDetails)) {
            this.dislikes.remove(profileDetails);
            this.totalDislikes--;
            return false;
        } else {
            if (this.likes.contains(profileDetails)) {
                this.likes.remove(profileDetails);
                this.totalLikes--;
            }
            this.dislikes.add(profileDetails);
            this.totalDislikes++;
            return true;
        }
    }
}
