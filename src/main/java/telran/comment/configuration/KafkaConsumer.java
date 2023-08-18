package telran.comment.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.comment.dto.accounting.ProfileDto;

import java.util.function.Consumer;

@Getter
@Configuration
public class KafkaConsumer {
    @Setter
    ProfileDto profile;

    @Bean
    protected Consumer<ProfileDto> receiveProfile() {
        return data -> {
            this.profile = data;
        };
    }

    //consumer for problem
}
