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
import telran.comment.dto.kafkaData.CommentServiceDataDto;
import telran.comment.dto.kafkaData.ProblemServiceDataDto;
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
        ProblemServiceDataDto problemData = kafkaConsumer.getProblemData();
        if (problemData.getProblemId().equals(problemId)) {
            comment.setAuthor(profile.getUsername());
            comment.setAuthorId(profile.getEmail());
            comment.setProblemId(problemData.getProblemId());
            commentRepository.save(comment);
            CommentServiceDataDto data = new CommentServiceDataDto(profile.getEmail(), problemData.getProblemId(), comment.getId(), "addComment");
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
            data = addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "addLike");
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
            data = addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "addLike");
            kafkaProducer.setCommentData(data);
            commentRepository.save(comment);
            return true;
        } else {
            comment.getReactions().removeLike();
            data = problem.getSubscribers().contains(profile.getEmail())
                    ? addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "removeLike")
                    : addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "removeLikeRemoveActivities");
            kafkaProducer.setCommentData(data);
            commentRepository.save(comment);
        }
        return false;
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
            data = addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "addDislike");
            kafkaProducer.setCommentData(data);
            return true;
        }
        boolean liked = profile.getActivities().get(commentId).getLiked();
        boolean disliked = profile.getActivities().get(commentId).getDisliked();
        if (!liked) {
            comment.getReactions().addDislike();
            if (disliked) {
                comment.getReactions().removeDislike();
            }
            data = addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "addDislike");
            kafkaProducer.setCommentData(data);
            commentRepository.save(comment);
            return true;
        } else {
            comment.getReactions().removeDislike();
            data = problem.getSubscribers().contains(profile.getEmail())
                    ? addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "removeDislike")
                    : addDataToTransfer(profile.getEmail(), problem.getProblemId(), commentId, "removeDislikeRemoveActivities");
            kafkaProducer.setCommentData(data);
            commentRepository.save(comment);
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
        ProblemServiceDataDto problem = kafkaConsumer.getProblemData();
        if (comment.getAuthorId().equals(profile.getEmail())) {
            CommentServiceDataDto data = new CommentServiceDataDto(profile.getEmail(), problem.getProblemId(), comment.getId(), "deleteComment");
            if (problem.getSubscribers().contains(profile.getEmail())) {
                data = new CommentServiceDataDto(profile.getEmail(), problem.getProblemId(), comment.getId(), "deleteCommentAndProblem");
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

    private CommentServiceDataDto addDataToTransfer(String profileId, String problemId, String commentId, String methodName) {
        return new CommentServiceDataDto(profileId, problemId, commentId, methodName);
    }
}
