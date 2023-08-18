package telran.comment.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.comment.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {

}
