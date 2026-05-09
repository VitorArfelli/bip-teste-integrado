package br.com.bip.beneficios.contract.exception;

public class BusinessException extends BeneficioContractException {
	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
		this("BUSINESS_RULE_VIOLATION", message);
	}

	protected BusinessException(String code, String message) {
		super(code, message);
	}
}
