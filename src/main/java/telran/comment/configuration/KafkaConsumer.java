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
import telran.comment.dto.kafkaData.ProblemDataDto.ProblemMethodName;
import telran.comment.dto.kafkaData.ProblemDataDto.ProblemServiceDataDto;

import java.util.Set;
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
    ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<ProfileDto> receiveProfile() {
        return data -> {
            if (data.getUsername().equals("DELETED_PROFILE")) {
                //profile was deleted ->
                commentCustomRepository.deleteCommentsByAuthorId(data.getEmail());
                this.profile = new ProfileDto();
            } else if (this.profile != null && data.getEmail().equals(profile.getEmail()) && !data.getUsername().equals(profile.getUsername())) {
                commentCustomRepository.changeAuthorName(data.getEmail(), data.getUsername());
                this.profile = data;
            } else this.profile = data;
        };
    }

    @Bean
    @Transactional
    protected Consumer<ProblemServiceDataDto> receiveDataFromProblem() {
        return data -> {
            String profileId = data.getAuthorizedProfileId();
            String problemId = data.getProblemId();
            ProblemMethodName method = data.getMethodName();
            Set<String> comments = data.getComments();
            Set<String> solutions = data.getSolutions();
            Set<String> subscribers = data.getSubscribers();
            if (method.equals(ProblemMethodName.DELETE_PROBLEM)) {
                commentCustomRepository.deleteCommentsByProblemId(problemId);
                this.problemData = new ProblemServiceDataDto();
            } else {
                this.problemData = data;
            }
        };
    }
}
