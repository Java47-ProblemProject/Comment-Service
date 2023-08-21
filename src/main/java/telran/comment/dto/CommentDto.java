package telran.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    protected String id;
    protected String author;
    protected String authorId;
    protected String problemId;
    protected String details;
    protected LocalDateTime dateCreated;
    protected ReactionsDto reactions;
    protected String type;
}
