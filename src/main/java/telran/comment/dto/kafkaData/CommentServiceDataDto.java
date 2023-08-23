package telran.comment.dto.kafkaData;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class CommentServiceDataDto {
    private String profileId;
    private String problemId;
    private String commentsId;
    private String methodName;
}
