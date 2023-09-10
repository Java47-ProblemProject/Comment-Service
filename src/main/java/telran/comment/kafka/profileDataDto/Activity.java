package telran.comment.kafka.profileDataDto;

import lombok.Getter;

import java.util.Set;

@Getter
public class Activity {
    protected String type;
    protected String problemId;
    protected Double rating;
    protected Set<String> action;
}
