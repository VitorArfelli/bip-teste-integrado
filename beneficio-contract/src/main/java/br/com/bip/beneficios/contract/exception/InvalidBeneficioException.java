package br.com.bip.beneficios.contract.exception;

public class InvalidBeneficioException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public InvalidBeneficioException(String message) {
		super("INVALID_BENEFICIO", message);
	}
}
