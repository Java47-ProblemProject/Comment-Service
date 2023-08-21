package telran.comment.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import telran.comment.configuration.KafkaConsumer;
import telran.comment.configuration.KafkaProducer;
import telran.comment.dao.CommentRepository;
import telran.comment.dto.CommentDto;
import telran.comment.dto.CreateEditCommentDto;
import telran.comment.dto.accounting.ActivityDto;
import telran.comment.dto.accounting.ProfileDto;
import telran.comment.dto.problem.ProblemDto;
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
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemDto problem = kafkaConsumer.getProblem();
        if (problem.getId().equals(problemId)) {
            comment.setAuthor(profile.getUsername());
            comment.setAuthorId(profile.getEmail());
            comment.setProblemId(problem.getId());
            commentRepository.save(comment);
            if (!profile.getActivities().containsKey(problemId)) {
                profile.addActivity(problem.getId(), new ActivityDto(problem.getType(), false, false));
            }
            profile.addActivity(comment.getId(), new ActivityDto(comment.getType(), false, false));
            editProfile(profile);
            kafkaProducer.setCommentIdToProblem(problemId + "," + comment.getId());
            kafkaProducer.sendUpdatedProfile();
            return modelMapper.map(comment, CommentDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Wrong problem in address");
    }

    @Override
    @Transactional
    public Boolean addLike(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemDto problem = kafkaConsumer.getProblem();
        checkProblemInActivities(profile, problem);
        ActivityDto activity = profile.getActivities().computeIfAbsent(commentId, a -> new ActivityDto(comment.getType(), false, false));
        if (!activity.getLiked()) {
            activity.setLiked(true);
            if (activity.getDisliked()) {
                activity.setDisliked(false);
                comment.getReactions().removeDislike();
            }
            comment.getReactions().addLike();
            commentRepository.save(comment);
            profile.addActivity(commentId, activity);
            editProfile(profile);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean addDislike(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemDto problem = kafkaConsumer.getProblem();
        checkProblemInActivities(profile, problem);
        ActivityDto activity = profile.getActivities().computeIfAbsent(commentId, a -> new ActivityDto(comment.getType(), false, false));
        if (!activity.getDisliked()) {
            activity.setDisliked(true);
            if (activity.getLiked()) {
                activity.setLiked(false);
                comment.getReactions().removeLike();
            }
            comment.getReactions().addDislike();
            commentRepository.save(comment);
            profile.addActivity(commentId, activity);
            editProfile(profile);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public CommentDto editComment(String problemId, String commentId, CreateEditCommentDto details) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (comment.getAuthorId().equals(profile.getEmail())) {
            comment.setDetails(details.getDetails());
            commentRepository.save(comment);
            return modelMapper.map(comment, CommentDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that comment");
    }

    @Override
    @Transactional
    public CommentDto deleteComment(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (comment.getAuthorId().equals(profile.getEmail())) {
            profile.removeActivity(commentId);
            //kafkaProducer.setCommentIdToDelete(commentId);   // Here should be sent to remove it from everyone
            editProfile(profile);
            commentRepository.delete(comment);
            return modelMapper.map(comment, CommentDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that comment");
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
        return commentRepository.findAll().stream().map(e -> modelMapper.map(e, CommentDto.class)).collect(Collectors.toSet());
    }

    private void editProfile(ProfileDto profile) {
        kafkaConsumer.setProfile(profile);
        kafkaProducer.setProfile(profile);
    }

    private void checkProblemInActivities(ProfileDto profile, ProblemDto problem) {
        if (!profile.getActivities().containsKey(problem.getId())) {
            profile.addActivity(problem.getId(), new ActivityDto(problem.getType(), false, false));
        }
    }
}
