package telran.comment.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.comment.configuration.KafkaConsumer;
import telran.comment.dao.CommentRepository;
import telran.comment.dto.accounting.ProfileDto;
import telran.comment.dto.problem.ProblemDto;
import telran.comment.model.Comment;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final CommentRepository commentRepository;

    public boolean checkCommentAuthorAndProblemId(String problemId, String commentId, String authorId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemDto problem = kafkaConsumer.getProblem();
        return authorId.equals(profile.getEmail()) && authorId.equals(comment.getAuthorId()) && problemId.equals(problem.getId());
    }

    public boolean checkProblemId(String problemId){
        ProblemDto problem = kafkaConsumer.getProblem();
        return problemId.equals(problem.getId());
    }
}
