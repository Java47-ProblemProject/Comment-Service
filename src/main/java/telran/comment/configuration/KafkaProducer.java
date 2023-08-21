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
    private String commentIdToDelete;

    @Bean
    public Supplier<ProfileDto> sendUpdatedProfile() {
        return () -> {
            if (profile != null) {
                //streamBridge.send("sendUpdatedProfile-out-0", profile);
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
                //streamBridge.send("sendCommentIdToProblem-out-0", commentIdToProblem);
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
            if (commentIdToDelete != null) {
                //streamBridge.send("sendCommentIdToDelete-out-0", commentIdToDelete);
                String sentMessage = commentIdToDelete;
                commentIdToDelete = null;
                return sentMessage;
            }
            return null;
        };
    }
}
