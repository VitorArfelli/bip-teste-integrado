package br.com.bip.beneficios.contract.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true, inherited = true)
public abstract class BeneficioContractException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String code;

	protected BeneficioContractException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
