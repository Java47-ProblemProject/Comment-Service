package telran.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.comment.dto.CommentDto;
import telran.comment.dto.CreateEditCommentDto;
import telran.comment.service.CommentService;

import java.util.Set;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    final CommentService commentService;

    @PutMapping("/addcomment/{problemId}")
    public CommentDto addComment(@PathVariable String problemId, @RequestBody CreateEditCommentDto details) {
        return commentService.addComment(problemId, details);
    }

    @PutMapping("/likecomment/{problemId}/{commentId}")
    public Boolean addLike(@PathVariable String problemId, @PathVariable String commentId) {
        return commentService.addLike(problemId, commentId);
    }

    @PutMapping("/dislikecomment/{problemId}/{commentId}")
    public Boolean addDislike(@PathVariable String problemId, @PathVariable String commentId) {
        return commentService.addDislike(problemId, commentId);
    }

    @PutMapping("/editcoment/{problemId}/{commentId}")
    public CommentDto editComment(@PathVariable String problemId, @PathVariable String commentId, @RequestBody CreateEditCommentDto details) {
        return commentService.editComment(problemId, commentId, details);
    }

    @DeleteMapping("/deletecomment/{profileId}/{problemId}/{commentId}")
    public CommentDto deleteComment(@PathVariable String profileId, @PathVariable String problemId, @PathVariable String commentId) {
        return commentService.deleteComment(problemId, commentId);
    }
    @GetMapping("/getcomment/{problemId}/{commentId}")
    public CommentDto getComment(@PathVariable String problemId, @PathVariable String commentId) {
        return commentService.getComment(problemId, commentId);
    }
    @GetMapping("/{problemId}/getcomments")
    public Set<CommentDto> getComments(@PathVariable String problemId) {
        return commentService.getComments(problemId);
    }
}
