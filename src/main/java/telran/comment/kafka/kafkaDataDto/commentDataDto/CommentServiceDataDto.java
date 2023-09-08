package telran.comment.kafka.kafkaDataDto.commentDataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentServiceDataDto {
    private String profileId;
    private String problemId;
    private Double problemRating;
    private String commentsId;
    private CommentMethodName methodName;
}
