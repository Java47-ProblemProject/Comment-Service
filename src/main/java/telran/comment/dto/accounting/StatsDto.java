package telran.comment.dto.accounting;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class StatsDto {
    @Setter
    protected Integer solvedProblems;
    protected Integer checkedSolutions;
    @Setter
    protected Integer formulatedProblems;
    protected Integer rating;
}