package telran.comment.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import telran.comment.dao.CommentRepository;
import telran.comment.kafka.KafkaConsumer;
import telran.comment.kafka.kafkaDataDto.ProblemDataDto.ProblemServiceDataDto;
import telran.comment.kafka.profileDataDto.ProfileDataDto;
import telran.comment.model.Comment;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final CommentRepository commentRepository;

    public boolean checkCommentAuthorAndProblemId(String problemId, String commentId, String authorId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problemData = kafkaConsumer.getProblemData();
        return authorId.equals(profile.getEmail()) && authorId.equals(comment.getAuthorId()) && problemId.equals(problemData.getProblemId());
    }

    public boolean checkProblemId(String problemId) {
        ProblemServiceDataDto problemData = kafkaConsumer.getProblemData();
        return problemId.equals(problemData.getProblemId());
    }
}
