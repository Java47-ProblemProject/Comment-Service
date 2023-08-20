package telran.comment.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.comment.dto.accounting.ProfileDto;
import telran.comment.dto.problem.ProblemDto;

import java.util.function.Consumer;

@Getter
@Configuration
public class KafkaConsumer {
    @Setter
    ProfileDto profile;
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
    protected Consumer<ProblemDto> receiveProblem() {
        return data -> {
            this.problem = data;
        };
    }
}
