package br.com.bip.beneficios.contract.exception;

public class InactiveBeneficioException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public InactiveBeneficioException(String papel) {
		super("BENEFICIO_INACTIVE", "Beneficio de " + papel + " esta inativo.");
	}
}
