package telran.comment.dto;

import lombok.Getter;
import telran.comment.model.ProfileDetails;

import java.util.Set;

@Getter
public class ReactionsDto {
    protected Integer totalLikes;
    protected Integer totalDislikes;
    protected Set<ProfileDetails> likes;
    protected Set<ProfileDetails> dislikes;
}
