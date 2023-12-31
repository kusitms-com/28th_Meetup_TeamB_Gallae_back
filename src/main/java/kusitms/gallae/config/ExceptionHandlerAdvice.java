package kusitms.gallae.config;

import jakarta.servlet.http.HttpServletRequest;
import kusitms.gallae.global.jwt.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e, HttpServletRequest request) {

        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(new BaseResponse<>(e.getStatus()));
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<BaseResponse<?>> handleJwtException(BaseException e, HttpServletRequest request) {

        return ResponseEntity.status(e.getStatus().getHttpStatus()).body(new BaseResponse<>(e.getStatus()));
    }

}
