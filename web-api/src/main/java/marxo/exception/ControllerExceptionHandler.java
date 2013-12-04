package marxo.exception;

import marxo.tool.Loggable;
import marxo.tool.StringTool;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.BindException;
import java.util.Arrays;

@ControllerAdvice
public class ControllerExceptionHandler implements Loggable {
	// Spring built-in
	@ExceptionHandler({BindException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, MissingServletRequestPartException.class, TypeMismatchException.class})
	public ResponseEntity<ErrorJson> handleBadRequest(Exception e) {
		logger.debug(e.getMessage());

		return new ResponseEntity<>(new ErrorJson(String.format("The request body is not acceptable [%s]", e.getClass().getSimpleName())), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
	public ResponseEntity<ErrorJson> handleInternalServerError(Exception e) {
		logger.error(e.getMessage());
		logger.error(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson("Maybe the developer forget to write a converter"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
	public ResponseEntity<ErrorJson> handleNotAcceptable(Exception e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(String.format("The request media type is not acceptable [%s]", e.getMessage())), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler({HttpMediaTypeNotSupportedException.class})
	public ResponseEntity<ErrorJson> handleUnsupportedMediaType(Exception e) {
		logger.debug(e.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("Doesn't support your shit [%s]", e.getMessage())), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	public ResponseEntity<ErrorJson> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
		logger.debug(e.getMessage());

		return new ResponseEntity<>(new ErrorJson(String.format("The request method [%s] is not supported. Acceptable methods are %s", e.getMethod(), Arrays.toString(e.getSupportedMethods()))), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({NoSuchRequestHandlingMethodException.class, IllegalArgumentException.class})
	public ResponseEntity<ErrorJson> handleNotFound(Exception e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(String.format("Cannot find your shit [%s] %s", e.getClass().getSimpleName(), StringTool.exceptionToString(e))), HttpStatus.NOT_FOUND);
	}

	// This application only
	@ExceptionHandler({EntityNotFoundException.class})
	public ResponseEntity<ErrorJson> handleEntityNotFoundException(EntityNotFoundException e) {
		logger.debug(e.getMessage());

		return new ResponseEntity<>(new ErrorJson(e.message), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({InvalidObjectIdException.class})
	public ResponseEntity<ErrorJson> handleInvalidObjectIdException(InvalidObjectIdException e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(e.message), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EntityInvalidException.class, EntityExistsException.class})
	public ResponseEntity<ErrorJson> handleEntityExistsException(EntityException e) {
		logger.debug(e.getMessage());
		return new ResponseEntity<>(new ErrorJson(e.messages.toArray(new String[e.messages.size()])), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ValidationException.class})
	public ResponseEntity<ErrorJson> handleValidationException(ValidationException e) {
		logger.debug(e.getMessage());
		return new ResponseEntity<>(new ErrorJson(e.reasons.toArray(new String[e.reasons.size()])), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EntityTypeException.class})
	public ResponseEntity<ErrorJson> handleEntityTypeException(EntityTypeException e) {
		logger.debug(e.getMessage());
		return new ResponseEntity<>(new ErrorJson(e.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({DataAccessResourceFailureException.class})
	public ResponseEntity<ErrorJson> handleDataAccessResourceFailureException(DataAccessResourceFailureException e) {
		logger.error(e.getMessage());
		logger.error(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson("Cannot connect to the database"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({NotImplementedException.class})
	public ResponseEntity<ErrorJson> handleNotImplementedException(NotImplementedException e) {
		return new ResponseEntity<>(new ErrorJson("Well... you are using a working-in-progress project. This part isn't done yet."), HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorJson> handleOtherException(Exception e) {
		logger.error(StringTool.exceptionToString(e));
		return new ResponseEntity<>(new ErrorJson("Congratulations! You broke the server: [" + e.getClass().getSimpleName() + "] " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
