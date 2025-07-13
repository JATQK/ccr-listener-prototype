package de.leipzig.htwk.gitrdf.listener.api.advice;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.leipzig.htwk.gitrdf.listener.api.exception.BadRequestException;
import de.leipzig.htwk.gitrdf.listener.api.exception.NotFoundException;
import de.leipzig.htwk.gitrdf.listener.api.exception.RatingsNotFoundException;
import de.leipzig.htwk.gitrdf.listener.api.model.response.error.BadRequestErrorResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.error.InternalServerErrorResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.error.NotFoundErrorResponse;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GeneralControllerAdvice {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<BadRequestErrorResponse> handleBadRequestException(BadRequestException ex) {

        log.info("Bad request exception during request handling.", ex);

        BadRequestErrorResponse response = new BadRequestErrorResponse(ex.getStatus(), ex.getReason(),
                ex.getSolution());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<NotFoundErrorResponse> handleNotFoundException(NotFoundException ex) {

        log.info("Not found exception during request handling.", ex);

        NotFoundErrorResponse response = new NotFoundErrorResponse(ex.getStatus(), ex.getReason(), ex.getSolution());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RatingsNotFoundException.class)
    public ResponseEntity<NotFoundErrorResponse> handleRatingsNotFoundException(RatingsNotFoundException ex) {

        log.info("Ratings not found exception during request handling.", ex);

        NotFoundErrorResponse response = new NotFoundErrorResponse(ex.getStatus(), ex.getReason(), ex.getSolution());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<InternalServerErrorResponse> handleIOException(IOException ex) {

        log.error("IOException during request handling.", ex);

        return new ResponseEntity<>(
                InternalServerErrorResponse.unexpectedException(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<InternalServerErrorResponse> handleSqlException(SQLException ex) {

        log.error("SQLException during request handling.", ex);

        return new ResponseEntity<>(
                InternalServerErrorResponse.unexpectedException(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<InternalServerErrorResponse> handleRuntimeException(RuntimeException ex) {

        log.error("RuntimeException during request handling.", ex);

        return new ResponseEntity<>(
                InternalServerErrorResponse.unexpectedException(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}