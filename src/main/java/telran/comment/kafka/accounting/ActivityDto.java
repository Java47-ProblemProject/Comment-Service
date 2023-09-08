package telran.comment.kafka.accounting;

import lombok.Getter;

import java.util.Set;

@Getter
public class ActivityDto {
    protected String type;
    protected String problemId;
    protected Double rating;
    protected Set<String> action;
}
