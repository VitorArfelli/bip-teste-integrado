package br.com.bip.beneficios.contract.exception;

public class ResourceNotFoundException extends BeneficioContractException {
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		this("RESOURCE_NOT_FOUND", message);
	}

	protected ResourceNotFoundException(String code, String message) {
		super(code, message);
	}
}
