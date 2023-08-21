package telran.comment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "Comments")
@Getter
@EqualsAndHashCode(of = "id")
@ToString
@AllArgsConstructor
public class Comment {
    @Id
    protected String id;
    @Setter
    protected String author;
    @Setter
    protected String authorId;
    @Setter
    protected String problemId;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    protected Reactions reactions;
    protected String type;

    public Comment() {
        this.dateCreated = LocalDateTime.now();
        this.reactions = new Reactions(0, 0);
        this.type = "COMMENT";
    }

    public void setReactionsLike(){
        this.reactions.setLikes(this.reactions.getLikes()+1);
    }

    public void setReactionsDislike(){
        this.reactions.setDislikes(this.reactions.getDislikes()+1);
    }
}
