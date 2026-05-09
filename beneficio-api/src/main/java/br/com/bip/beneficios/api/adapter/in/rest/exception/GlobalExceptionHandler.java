package br.com.bip.beneficios.api.adapter.in.rest.exception;

import br.com.bip.beneficios.api.adapter.out.ejb.EjbIntegrationException;
import br.com.bip.beneficios.api.config.CorrelationIdFilter;
import br.com.bip.beneficios.contract.exception.BeneficioContractException;
import br.com.bip.beneficios.contract.exception.BusinessException;
import br.com.bip.beneficios.contract.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException.class)
	ResponseEntity<ApiError> handleBusiness(BusinessException exception, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, exception.getCode(), List.of(exception.getMessage()), request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, exception.getCode(), List.of(exception.getMessage()), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		List<String> messages = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();
		return build(HttpStatus.BAD_REQUEST, "REQUEST_VALIDATION_ERROR", messages, request);
	}

	@ExceptionHandler(EjbIntegrationException.class)
	ResponseEntity<ApiError> handleIntegration(EjbIntegrationException exception, HttpServletRequest request) {
		return build(HttpStatus.SERVICE_UNAVAILABLE, "EJB_INTEGRATION_ERROR", List.of(exception.getMessage()), request);
	}

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<ApiError> handleRuntime(RuntimeException exception, HttpServletRequest request) {
		BeneficioContractException contractException = findContractException(exception);
		if (contractException instanceof ResourceNotFoundException) {
			return build(HttpStatus.NOT_FOUND, contractException.getCode(), List.of(contractException.getMessage()),
					request);
		}
		if (contractException instanceof BusinessException) {
			return build(HttpStatus.BAD_REQUEST, contractException.getCode(), List.of(contractException.getMessage()),
					request);
		}
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", List.of("Erro interno inesperado."),
				request);
	}

	private ResponseEntity<ApiError> build(HttpStatus status, String code, List<String> messages,
			HttpServletRequest request) {
		ApiError error = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), code,
				correlationId(request), messages, request.getRequestURI());
		return ResponseEntity.status(status).body(error);
	}

	private BeneficioContractException findContractException(Throwable exception) {
		Throwable current = exception;
		while (current != null) {
			if (current instanceof BeneficioContractException contractException) {
				return contractException;
			}
			current = current.getCause();
		}
		return null;
	}

	private String correlationId(HttpServletRequest request) {
		Object value = request.getAttribute(CorrelationIdFilter.ATTRIBUTE_NAME);
		return value == null ? null : value.toString();
	}
}
