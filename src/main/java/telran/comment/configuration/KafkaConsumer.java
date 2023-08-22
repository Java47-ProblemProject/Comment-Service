package telran.comment.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.comment.dao.CommentCustomRepository;
import telran.comment.dao.CommentRepository;
import telran.comment.dto.accounting.ProfileDto;
import telran.comment.dto.problem.ProblemDto;

import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    final CommentCustomRepository commentCustomRepository;
    final CommentRepository commentRepository;
    final KafkaProducer kafkaProducer;
    @Setter
    ProfileDto profile;
    @Setter
    ProblemDto problem;

    @Bean
    @Transactional
    protected Consumer<ProfileDto> receiveProfile() {
        return data -> {
            this.profile = data;
        };
    }

    @Bean
    @Transactional
    protected Consumer<ProfileDto> receiveUpdatedProfile() {
        return data -> {
            this.profile = data;
        };
    }

    @Bean
    @Transactional
    protected Consumer<ProblemDto> receiveProblem() {
        return data -> {
            this.problem = data;
        };
    }

    @Bean
    @Transactional
    protected Consumer<ProblemDto> receiveCommentIdToDelete() {
        return data -> {
            String problemId = data.getId();
            commentCustomRepository.deleteCommentsByProblemId(problemId);
        };
    }

    @Bean
    @Transactional
    protected Consumer<String> receiveNewName() {
        return data ->{
            String authorId = data.split(",")[0];
            String newName = data.split(",")[1];
            commentCustomRepository.changeAuthorName(authorId, newName);
        };
    }

    @Bean
    @Transactional
    protected Consumer<String> receiveAuthorToRemove() {
        return commentCustomRepository::deleteCommentsByAuthorId;
    }
}
