package telran.comment.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.comment.dao.CommentCustomRepository;
import telran.comment.dao.CommentRepository;
import telran.comment.kafka.kafkaDataDto.ProblemDataDto.ProblemMethodName;
import telran.comment.kafka.kafkaDataDto.ProblemDataDto.ProblemServiceDataDto;
import telran.comment.kafka.profileDataDto.ProfileDataDto;
import telran.comment.kafka.profileDataDto.ProfileMethodName;
import telran.comment.security.JwtTokenService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CommentCustomRepository commentCustomRepository;
    private final CommentRepository commentRepository;
    private final JwtTokenService jwtTokenService;
    private final Map<String, ProfileDataDto> profiles = new ConcurrentHashMap<>();
    private ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<ProfileDataDto> receiveProfile() {
        return data -> {
            String email = data.getEmail();
            String userName = data.getUserName();
            ProfileMethodName methodName = data.getMethodName();
            ProfileDataDto profile = this.profiles.get(email);
            if (!profiles.containsKey(email)) {
                this.profiles.put(email, data);
                profile = data;
            }
            if (methodName.equals(ProfileMethodName.SET_PROFILE)) {
                jwtTokenService.setCurrentProfileToken(email, data.getToken());
                this.profiles.get(email).setToken("");
            } else if (methodName.equals(ProfileMethodName.UNSET_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                this.profiles.remove(email);
            } else if (methodName.equals(ProfileMethodName.UPDATED_PROFILE)) {
                this.profiles.put(email, profile);
            } else if (methodName.equals(ProfileMethodName.EDIT_PROFILE_NAME)) {
                commentCustomRepository.changeAuthorName(email, userName);
                this.profiles.get(email).setUserName(profile.getUserName());
            } else if (methodName.equals(ProfileMethodName.DELETE_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                commentCustomRepository.deleteCommentsByAuthorId(email);
                this.profiles.remove(email);
            }
        };
    }

    @Bean
    @Transactional
    protected Consumer<ProblemServiceDataDto> receiveDataFromProblem() {
        return data -> {
            String problemId = data.getProblemId();
            ProblemMethodName method = data.getMethodName();
            if (method.equals(ProblemMethodName.DELETE_PROBLEM)) {
                commentCustomRepository.deleteCommentsByProblemId(problemId);
                this.problemData = new ProblemServiceDataDto();
            } else {
                this.problemData = data;
            }
        };
    }
}
