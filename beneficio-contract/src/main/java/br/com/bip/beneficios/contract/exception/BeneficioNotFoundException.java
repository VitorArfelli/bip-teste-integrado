package br.com.bip.beneficios.contract.exception;

public class BeneficioNotFoundException extends ResourceNotFoundException {
	private static final long serialVersionUID = 1L;

	public BeneficioNotFoundException(Long id) {
		super("BENEFICIO_NOT_FOUND", id == null ? "Beneficio nao encontrado." : "Beneficio " + id + " nao encontrado.");
	}
}
