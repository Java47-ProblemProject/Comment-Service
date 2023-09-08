package telran.comment.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.comment.dao.CommentCustomRepository;
import telran.comment.dao.CommentRepository;
import telran.comment.kafka.accounting.ProfileDto;
import telran.comment.kafka.kafkaDataDto.ProblemDataDto.ProblemMethodName;
import telran.comment.kafka.kafkaDataDto.ProblemDataDto.ProblemServiceDataDto;
import telran.comment.security.JwtTokenService;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CommentCustomRepository commentCustomRepository;
    private final CommentRepository commentRepository;
    private final JwtTokenService jwtTokenService;
    private ProfileDto profile;
    private String token;
    private ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<Map<String, ProfileDto>> receiveProfile() {
        return data -> {
            if (!data.isEmpty()) {
                Map.Entry<String, ProfileDto> entry = data.entrySet().iterator().next();
                if (entry.getValue().getUsername().equals("DELETED_PROFILE")) {
                    //profile was deleted ->
                    jwtTokenService.deleteCurrentProfileToken(entry.getValue().getEmail());
                    entry.getValue().getActivities().entrySet().stream()
                            .filter(e -> "PROBLEM".equals(e.getValue().getType()) && e.getValue().getAction().contains("AUTHOR"))
                            .map(Map.Entry::getKey)
                            .forEach(commentCustomRepository::deleteCommentsByProblemId);
                    this.profile = null;
                    this.token = null;
                } else {
                    if (this.profile != null && entry.getValue().getEmail().equals(this.profile.getEmail()) && !entry.getValue().getUsername().equals(this.profile.getUsername())) {
                        commentCustomRepository.changeAuthorName(entry.getValue().getEmail(), entry.getValue().getUsername());
                    }
                    this.profile = entry.getValue();
                    if (!entry.getKey().isEmpty()) {
                        this.token = entry.getKey();
                    }
                    jwtTokenService.setCurrentProfileToken(this.profile.getEmail(), this.token);
                    System.out.println("Token pushed - " + this.token);
                }
            }
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
