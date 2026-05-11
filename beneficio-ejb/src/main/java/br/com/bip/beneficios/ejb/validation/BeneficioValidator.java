package br.com.bip.beneficios.ejb.validation;

import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.BeneficioUpdateRequestDto;
import br.com.bip.beneficios.contract.exception.InactiveBeneficioException;
import br.com.bip.beneficios.contract.exception.InsufficientBalanceException;
import br.com.bip.beneficios.contract.exception.InvalidBeneficioException;
import br.com.bip.beneficios.contract.exception.InvalidTransferenciaException;
import br.com.bip.beneficios.ejb.entity.Beneficio;

/**
 * Valida regras de negócio antes da persistência e da transferência de saldos.
 */
public final class BeneficioValidator {
	private BeneficioValidator() {
	}

	/**
	 * Valida os dados de criação ou atualização de benefício.
	 *
	 * @param request dados recebidos pelo contrato remoto.
	 */
	public static void validarBeneficio(BeneficioRequestDto request) {
		if (request == null) {
			throw new InvalidBeneficioException("Dados do beneficio sao obrigatorios.");
		}
		if (request.getNome() == null || request.getNome().trim().isEmpty()) {
			throw new InvalidBeneficioException("Nome do beneficio e obrigatorio.");
		}
		if (request.getNome().length() > 100) {
			throw new InvalidBeneficioException("Nome do beneficio deve ter no maximo 100 caracteres.");
		}
		if (request.getDescricao() != null && request.getDescricao().length() > 255) {
			throw new InvalidBeneficioException("Descricao do beneficio deve ter no maximo 255 caracteres.");
		}
		if (request.getValorCentavos() == null) {
			throw new InvalidBeneficioException("Valor do beneficio e obrigatorio.");
		}
		if (request.getValorCentavos() < 0) {
			throw new InvalidBeneficioException("Valor do beneficio nao pode ser negativo.");
		}
	}

	/**
	 * Valida os dados cadastrais de atualização sem permitir alteração direta de saldo.
	 *
	 * @param request dados recebidos pelo contrato remoto.
	 */
	public static void validarAtualizacaoBeneficio(BeneficioUpdateRequestDto request) {
		if (request == null) {
			throw new InvalidBeneficioException("Dados do beneficio sao obrigatorios.");
		}
		if (request.getNome() == null || request.getNome().trim().isEmpty()) {
			throw new InvalidBeneficioException("Nome do beneficio e obrigatorio.");
		}
		if (request.getNome().length() > 100) {
			throw new InvalidBeneficioException("Nome do beneficio deve ter no maximo 100 caracteres.");
		}
		if (request.getDescricao() != null && request.getDescricao().length() > 255) {
			throw new InvalidBeneficioException("Descricao do beneficio deve ter no maximo 255 caracteres.");
		}
	}

	/**
	 * Valida os parâmetros básicos da transferência.
	 *
	 * @param origemId      identificador do benefício de origem.
	 * @param destinoId     identificador do benefício de destino.
	 * @param valorCentavos valor transferido em centavos.
	 */
	public static void validarTransferencia(Long origemId, Long destinoId, Long valorCentavos) {
		if (origemId == null) {
			throw new InvalidTransferenciaException("Beneficio de origem e obrigatorio.");
		}
		if (destinoId == null) {
			throw new InvalidTransferenciaException("Beneficio de destino e obrigatorio.");
		}
		if (origemId.equals(destinoId)) {
			throw new InvalidTransferenciaException("Beneficio de origem e destino devem ser diferentes.");
		}
		if (valorCentavos == null) {
			throw new InvalidTransferenciaException("Valor da transferencia e obrigatorio.");
		}
		if (valorCentavos <= 0) {
			throw new InvalidTransferenciaException("Valor da transferencia deve ser maior que zero.");
		}
	}

	/**
	 * Garante que o benefício participa de operações de transferência.
	 *
	 * @param beneficio benefício validado.
	 * @param papel     papel no fluxo, como origem ou destino.
	 */
	public static void validarBeneficioAtivo(Beneficio beneficio, String papel) {
		if (!beneficio.isAtivo()) {
			throw new InactiveBeneficioException(papel);
		}
	}

	/**
	 * Garante que o benefício de origem possui saldo suficiente.
	 *
	 * @param origem        benefício debitado.
	 * @param valorCentavos valor transferido em centavos.
	 */
	public static void validarSaldo(Beneficio origem, Long valorCentavos) {
		if (origem.getValorCentavos() < valorCentavos) {
			throw new InsufficientBalanceException();
		}
	}
}
