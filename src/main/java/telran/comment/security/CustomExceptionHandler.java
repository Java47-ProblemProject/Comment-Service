package telran.comment.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import telran.comment.dto.exception.ExceptionDto;

import java.util.NoSuchElementException;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleProfileExistsException(NoSuchElementException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(), "Bad Request");
        exceptionDto.setMessage("Comment is not exists.");
        exceptionDto.setPath("/comment/*");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }
}
