package telran.comment.dto.accounting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class ActivityDto {
    @Setter
    protected String type;
    @Setter
    protected Boolean liked;
    @Setter
    protected Boolean disliked;
}