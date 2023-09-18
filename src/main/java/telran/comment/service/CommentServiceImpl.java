package telran.comment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import telran.comment.kafka.KafkaConsumer;
import telran.comment.kafka.KafkaProducer;
import telran.comment.dao.CommentRepository;
import telran.comment.dto.CommentDto;
import telran.comment.dto.CreateEditCommentDto;
import telran.comment.kafka.kafkaDataDto.commentDataDto.CommentMethodName;
import telran.comment.kafka.kafkaDataDto.commentDataDto.CommentServiceDataDto;
import telran.comment.kafka.kafkaDataDto.ProblemDataDto.ProblemServiceDataDto;
import telran.comment.kafka.profileDataDto.ProfileDataDto;
import telran.comment.model.Comment;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    final CommentRepository commentRepository;
    final KafkaConsumer kafkaConsumer;
    final KafkaProducer kafkaProducer;
    final ModelMapper modelMapper;

    @Override
    @Transactional
    public CommentDto addComment(String problemId, CreateEditCommentDto details) {
        Comment comment = modelMapper.map(details, Comment.class);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        comment.setAuthor(profile.getUserName());
        comment.setAuthorId(profile.getEmail());
        comment.setProblemId(problem.getProblemId());
        commentRepository.save(comment);
        transferData(profile, problem, comment, CommentMethodName.ADD_COMMENT);
        return modelMapper.map(comment, CommentDto.class);

    }

    @Override
    @Transactional
    public Boolean addLike(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getRating();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        boolean result = comment.getReactions().setLike(profile.getEmail(), profileRating);
        commentRepository.save(comment);
        transferData(profile, problem, comment, CommentMethodName.ADD_LIKE);
        return result;
    }

    @Override
    @Transactional
    public Boolean addDislike(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getRating();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        boolean result = comment.getReactions().setDislike(profile.getEmail(), profileRating);
        commentRepository.save(comment);
        transferData(profile, problem, comment, CommentMethodName.ADD_DISLIKE);
        return result;
    }

    @Override
    @Transactional
    public CommentDto editComment(String problemId, String commentId, CreateEditCommentDto details) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.setDetails(details.getDetails());
        commentRepository.save(comment);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    @Transactional
    public CommentDto deleteComment(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        transferData(profile, problem, comment, CommentMethodName.DELETE_COMMENT);
        commentRepository.delete(comment);
        return modelMapper.map(comment, CommentDto.class);

    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getComment(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CommentDto> getComments(String problemId) {
        return commentRepository.findAllByProblemId(problemId).map(e -> modelMapper.map(e, CommentDto.class)).collect(Collectors.toSet());
    }

    @Override
    public Set<CommentDto> getCommentsByProfileId(String profileId) {
        return commentRepository.findAllByAuthorId(profileId).map(e -> modelMapper.map(e, CommentDto.class)).collect(Collectors.toSet());
    }

    private void transferData(ProfileDataDto profile, ProblemServiceDataDto problem, Comment comment, CommentMethodName methodName) {
        CommentServiceDataDto commentData = new CommentServiceDataDto(profile.getEmail(), problem.getProblemId(), problem.getProblemRating(), comment.getId(), methodName);
        kafkaProducer.setCommentData(commentData);
    }
}
