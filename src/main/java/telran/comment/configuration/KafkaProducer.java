package telran.comment.configuration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.comment.dto.accounting.ProfileDto;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final StreamBridge streamBridge;
    @Setter
    private ProfileDto profile;
    @Setter
    private String commentIdToProblem;
    @Setter
    private String commentIdDelete;

    @Bean
    public Supplier<ProfileDto> sendUpdatedProfile() {
        return () -> {
            if (profile != null) {
                ProfileDto sentMessage = profile;
                profile = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<String> sendCommentIdToProblem() {
        return () -> {
            if (commentIdToProblem != null) {
                String sentMessage = commentIdToProblem;
                commentIdToProblem = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<String> sendCommentIdToDelete() {
        return () -> {
            if (commentIdDelete != null) {
                String sentMessage = commentIdDelete;
                commentIdDelete = null;
                return sentMessage;
            }
            return null;
        };
    }
}
