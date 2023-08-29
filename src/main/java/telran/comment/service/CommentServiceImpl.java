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
import telran.comment.dto.accounting.ProfileDto;
import telran.comment.dto.kafkaData.commentDataDto.CommentMethodName;
import telran.comment.dto.kafkaData.commentDataDto.CommentServiceDataDto;
import telran.comment.dto.kafkaData.ProblemDataDto.ProblemServiceDataDto;
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
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        if (problem.getProblemId().equals(problemId)) {
            comment.setAuthor(profile.getUsername());
            comment.setAuthorId(profile.getEmail());
            comment.setProblemId(problem.getProblemId());
            commentRepository.save(comment);
            CommentServiceDataDto data = addDataToTransfer(profile, problem, comment, CommentMethodName.ADD_COMMENT);
            kafkaProducer.setCommentData(data);
            return modelMapper.map(comment, CommentDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Wrong problem in address");
    }

    @Override
    @Transactional
    public Boolean addLike(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        CommentServiceDataDto data;
        boolean hasActivity = profile.getActivities().containsKey(commentId);
        if (!hasActivity) {
            comment.getReactions().addLike();
            commentRepository.save(comment);
            data = addDataToTransfer(profile, problem, comment, CommentMethodName.ADD_LIKE);
            kafkaProducer.setCommentData(data);
            return true;
        }
        boolean liked = profile.getActivities().get(commentId).getLiked();
        boolean disliked = profile.getActivities().get(commentId).getDisliked();
        if (!liked) {
            comment.getReactions().addLike();
            if (disliked) {
                comment.getReactions().removeDislike();
            }
            data = addDataToTransfer(profile, problem, comment, CommentMethodName.ADD_LIKE);
            kafkaProducer.setCommentData(data);
            commentRepository.save(comment);
            return true;
        } else {
            boolean isSubscriber = problem.getSubscribers().contains(profile.getEmail());
            boolean isAuthorProblem = problem.getProblemAuthorId().equals(profile.getEmail());
            boolean isAuthorComment = comment.getAuthorId().equals(profile.getEmail());
            if (isAuthorComment) {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.REMOVE_LIKE);
            } else if (isAuthorProblem || isSubscriber) {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.REMOVE_LIKE_REMOVE_COMMENT_ACTIVITY);
            } else {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.REMOVE_LIKE_REMOVE_ALL_ACTIVITIES);
            }
            kafkaProducer.setCommentData(data);
            comment.getReactions().removeLike();
            commentRepository.save(comment);
            return false;
        }
    }


    @Override
    @Transactional
    public Boolean addDislike(String problemId, String commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        CommentServiceDataDto data;
        boolean hasActivity = profile.getActivities().containsKey(commentId);
        if (!hasActivity) {
            comment.getReactions().addDislike();
            commentRepository.save(comment);
            data = addDataToTransfer(profile, problem, comment, CommentMethodName.ADD_DISLIKE);
            kafkaProducer.setCommentData(data);
            return true;
        }
        boolean liked = profile.getActivities().get(commentId).getLiked();
        boolean disliked = profile.getActivities().get(commentId).getDisliked();
        if (!disliked) {
            comment.getReactions().addDislike();
            if (liked) {
                comment.getReactions().removeLike();
            }
            data = addDataToTransfer(profile, problem, comment, CommentMethodName.ADD_DISLIKE);
            kafkaProducer.setCommentData(data);
            commentRepository.save(comment);
            return true;
        } else {
            boolean isSubscriber = problem.getSubscribers().contains(profile.getEmail());
            boolean isAuthorProblem = problem.getProblemAuthorId().equals(profile.getEmail());
            boolean isAuthorComment = comment.getAuthorId().equals(profile.getEmail());
            if (isAuthorComment) {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.REMOVE_DISLIKE);
            } else if (isAuthorProblem || isSubscriber) {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.REMOVE_DISLIKE_REMOVE_COMMENT_ACTIVITY);
            } else {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.REMOVE_DISLIKE_REMOVE_ALL_ACTIVITIES);
            }
            kafkaProducer.setCommentData(data);
            comment.getReactions().removeLike();
            commentRepository.save(comment);
            return false;
        }
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
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        if (comment.getAuthorId().equals(profile.getEmail())) {
            CommentServiceDataDto data = addDataToTransfer(profile, problem, comment, CommentMethodName.DELETE_COMMENT);
            if (problem.getSubscribers().contains(profile.getEmail())) {
                data = addDataToTransfer(profile, problem, comment, CommentMethodName.DELETE_COMMENT_AND_PROBLEM);
            }
            kafkaProducer.setCommentData(data);
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

    @Override
    public Set<CommentDto> getCommentsByProfileId(String profileId) {
        return commentRepository.findAllByAuthorId(profileId).map(e -> modelMapper.map(e, CommentDto.class)).collect(Collectors.toSet());
    }

    private CommentServiceDataDto addDataToTransfer(ProfileDto profile, ProblemServiceDataDto problem, Comment comment, CommentMethodName methodName) {
        return new CommentServiceDataDto(profile.getEmail(), problem.getProblemId(), problem.getProblemRating(), comment.getId(), methodName);
    }
}
