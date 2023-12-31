package telran.comment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.comment.kafka.kafkaDataDto.commentDataDto.CommentServiceDataDto;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    @Setter
    private CommentServiceDataDto commentData;

    @Bean
    public Supplier<CommentServiceDataDto> sendData() {
        return () -> {
            if (commentData != null) {
                CommentServiceDataDto sentMessage = commentData;
                commentData = null;
                return sentMessage;
            }
            return null;
        };
    }
}
