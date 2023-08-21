package telran.comment.dao;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import telran.comment.model.Comment;

import java.util.HashSet;
import java.util.Set;

@Repository
@AllArgsConstructor
public class CommentCustomRepository {
    private final MongoTemplate mongoTemplate;
    public void changeAuthorName(String profileId, String newName) {
        Query query = new Query(Criteria.where("authorId").is(profileId));
        Update update = new Update().set("author", newName);
        mongoTemplate.updateMulti(query, update, Comment.class);
    }
    public void deleteCommentsByProblemId(String problemId) {
        Query query = new Query(Criteria.where("problemId").is(problemId));
        mongoTemplate.remove(query, Comment.class);
    }

    public void deleteCommentsByAuthorId(String authorId) {
        Query query = new Query(Criteria.where("authorId").is(authorId));
        mongoTemplate.remove(query, Comment.class);
    }
}
