package telran.comment.service;

import telran.comment.dto.CommentDto;
import telran.comment.dto.CreateEditCommentDto;

import java.util.Set;

public interface CommentService {
    CommentDto addComment(String problemId, CreateEditCommentDto details);
    Boolean addLike(String problemId, String commentId);
    Boolean addDislike(String problemId, String commentId);
    CommentDto editComment(String problemId, String commentId, CreateEditCommentDto details);
    CommentDto deleteComment(String problemId, String commentId);
    CommentDto getComment(String problemId, String commentId);
    Set<CommentDto> getComments(String problemId);

    Set<CommentDto> getCommentsByProfileId(String profileId);
}
