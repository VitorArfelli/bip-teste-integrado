package br.com.bip.beneficios.contract.exception;

public class InvalidTransferenciaException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public InvalidTransferenciaException(String message) {
		super("INVALID_TRANSFERENCIA", message);
	}
}
