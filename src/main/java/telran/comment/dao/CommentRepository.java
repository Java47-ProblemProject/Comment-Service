package telran.comment.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.comment.model.Comment;

import java.util.stream.Stream;

public interface CommentRepository extends MongoRepository<Comment, String> {

    Stream<Comment> findAllByAuthorIdOrderByDateCreatedDesc(String profileId);
    Stream<Comment> findAllByProblemIdOrderByDateCreatedDesc(String problemId);
}
