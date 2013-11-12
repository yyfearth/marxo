package marxo.exception;

import marxo.tool.ILoggable;
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

@ControllerAdvice
public class ControllerExceptionHandler implements ILoggable {
	// Spring built-in
	@ExceptionHandler({BindException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, MissingServletRequestPartException.class, TypeMismatchException.class})
	public ResponseEntity<ErrorJson> handleBadRequest(Exception e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(String.format("The shit you gave is bad (%s)", e.getClass().getSimpleName())), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
	public ResponseEntity<ErrorJson> handleInternalServerError(Exception e) {
		logger.error(e.getMessage());
		logger.error(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson("Sorry, I don't know how to translate your shit"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
	public ResponseEntity<ErrorJson> handleNotAcceptable(Exception e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(String.format("Cannot accept your shit (%s)", e.getMessage())), HttpStatus.NOT_ACCEPTABLE);
	}

	@ExceptionHandler({HttpMediaTypeNotSupportedException.class})
	public ResponseEntity<ErrorJson> handleUnsupportedMediaType(Exception e) {
		logger.debug(e.getMessage());
		return new ResponseEntity<>(new ErrorJson(String.format("Doesn't support your shit (%s)", e.getMessage())), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler({HttpRequestMethodNotSupportedException.class})
	public ResponseEntity<ErrorJson> handleMethodNotAllowed(Exception e) {
		logger.debug(e.getMessage());

		return new ResponseEntity<>(new ErrorJson(String.format("Your shit is not allowed (%s)", e.getMessage())), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({NoSuchRequestHandlingMethodException.class, IllegalArgumentException.class})
	public ResponseEntity<ErrorJson> handleNotFound(Exception e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(String.format("Cannot find your shit [%s] %s", e.getMessage(), StringTool.exceptionToString(e))), HttpStatus.NOT_FOUND);
	}

	// This application only
	@ExceptionHandler({InvalidObjectIdException.class})
	public ResponseEntity<ErrorJson> handleInvalidObjectIdException(InvalidObjectIdException e) {
		logger.debug(e.getMessage());
		logger.debug(StringTool.exceptionToString(e));

		return new ResponseEntity<>(new ErrorJson(e.message), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EntityInvalidException.class, EntityExistsException.class, EntityNotFoundException.class})
	public ResponseEntity<ErrorJson> handleEntityExistsException(EntityException e) {
		logger.debug(e.getMessage());
		return new ResponseEntity<>(new ErrorJson(e.messages.toArray(new String[e.messages.size()])), HttpStatus.BAD_REQUEST);
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
