package telran.comment.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import telran.comment.model.Comment;

import java.util.stream.Stream;

public interface CommentRepository extends MongoRepository<Comment, String> {

    Stream<Comment> findAllByAuthorId(String profileId);
}
