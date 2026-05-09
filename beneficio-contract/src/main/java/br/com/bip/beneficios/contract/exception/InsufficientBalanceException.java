package br.com.bip.beneficios.contract.exception;

public class InsufficientBalanceException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public InsufficientBalanceException() {
		super("INSUFFICIENT_BALANCE", "Saldo insuficiente para transferencia.");
	}
}
