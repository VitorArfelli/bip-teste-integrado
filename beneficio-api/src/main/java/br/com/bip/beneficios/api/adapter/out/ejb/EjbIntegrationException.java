package br.com.bip.beneficios.api.adapter.out.ejb;

public class EjbIntegrationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EjbIntegrationException(String message, Throwable cause) {
		super(message, cause);
	}
}
