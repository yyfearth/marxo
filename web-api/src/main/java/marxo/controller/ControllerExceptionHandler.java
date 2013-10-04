package marxo.controller;

import marxo.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import java.net.BindException;

@ControllerAdvice
public class ControllerExceptionHandler {
	final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	// Spring built-in
	@ExceptionHandler({BindException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, MissingServletRequestPartException.class, TypeMismatchException.class})
	public ResponseEntity<ErrorJson> handleBadRequest(Exception ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("The shit you gave is bad (%s)", ex.getClass().getSimpleName())), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
	public ResponseEntity<ErrorJson> handleInternalServerError(Exception ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson("Sorry, I don't know how to translate your shit"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
	public ResponseEntity<ErrorJson> handleNotAcceptable(Exception ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("Cannot accept your shit (%s)", ex.getMessage())), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler({HttpMediaTypeNotSupportedException.class})
	public ResponseEntity<ErrorJson> handleUnsupportedMediaType(Exception ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("Doesn't support your shit (%s)", ex.getMessage())), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	public ResponseEntity<ErrorJson> handleMethodNotAllowed(Exception ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("Your shit is not allowed (%s)", ex.getMessage())), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({NoSuchRequestHandlingMethodException.class})
	public ResponseEntity<ErrorJson> handleNotFound(Exception ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("Cannot find your shit (%s)", ex.getMessage())), HttpStatus.NOT_FOUND);
	}

    // This application only
	@ExceptionHandler({InvalidObjectIdException.class})
	public ResponseEntity<ErrorJson> handleInvalidObjectIdException(InvalidObjectIdException ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(ex.message), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EntityInvalidException.class, EntityExistsException.class, EntityNotFoundException.class})
	public ResponseEntity<ErrorJson> handleEntityExistsException(EntityException ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson(ex.message), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({DataAccessResourceFailureException.class})
	public ResponseEntity<ErrorJson> handleDataAccessResourceFailureException(DataAccessResourceFailureException ex) {
		logger.debug(ex.getMessage());
		return new ResponseEntity<>(new ErrorJson("Cannot connect to the database"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorJson> handleOtherException(Exception ex) {
        logger.debug(ex.getMessage());
        return new ResponseEntity<>(new ErrorJson("Something is really wrong: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
