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

import java.util.function.Consumer;

@Getter
@Configuration
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CommentCustomRepository commentCustomRepository;
    private final CommentRepository commentRepository;
    private final JwtTokenService jwtTokenService;
    private ProfileDataDto profile;
    private ProblemServiceDataDto problemData;

    @Bean
    @Transactional
    protected Consumer<ProfileDataDto> receiveProfile() {
        return data -> {
            ProfileMethodName methodName = data.getMethodName();
            String userName = data.getUserName();
            String email = data.getEmail();
            //Double rating = data.getRating();
            if (methodName.equals(ProfileMethodName.SET_PROFILE)) {
                jwtTokenService.setCurrentProfileToken(data.getEmail(), data.getToken());
                this.profile = data;
                this.profile.setToken("");
            }
            if (methodName.equals(ProfileMethodName.UNSET_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                this.profile = null;
            }
            if (methodName.equals(ProfileMethodName.UPDATED_PROFILE)){
                this.profile = data;
            }
            if (methodName.equals(ProfileMethodName.EDIT_PROFILE_NAME)) {
                commentCustomRepository.changeAuthorName(email, userName);
                this.profile.setUserName(data.getUserName());
            }
            if (methodName.equals(ProfileMethodName.EDIT_PROFILE_EDUCATION)) {
                //problemCustomRepository.setNewProfileRating(rating);
            }
            if (methodName.equals(ProfileMethodName.DELETE_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                commentCustomRepository.deleteCommentsByAuthorId(email);
                this.profile = null;
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
