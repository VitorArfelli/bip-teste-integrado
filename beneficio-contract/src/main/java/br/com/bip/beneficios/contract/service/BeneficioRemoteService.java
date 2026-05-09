package br.com.bip.beneficios.contract.service;

import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import java.util.List;

/**
 * Contrato remoto exposto pelo EJB de benefícios.
 *
 * <p>
 * Este contrato é compartilhado entre o módulo EJB e a API REST. Ele mantém a
 * API Spring Boot desacoplada das entidades JPA e do mecanismo de lookup JNDI.
 */
public interface BeneficioRemoteService {
	/**
	 * Verifica se o EJB remoto está acessível.
	 *
	 * @return mensagem simples de disponibilidade.
	 */
	String ping();

	/**
	 * Lista os benefícios cadastrados.
	 *
	 * @return benefícios ordenados por identificador.
	 */
	List<BeneficioDto> listar();

	/**
	 * Busca um benefício por identificador.
	 *
	 * @param id identificador do benefício.
	 * @return benefício encontrado.
	 */
	BeneficioDto buscarPorId(Long id);

	/**
	 * Cria um benefício.
	 *
	 * @param request dados de criação.
	 * @return benefício criado.
	 */
	BeneficioDto criar(BeneficioRequestDto request);

	/**
	 * Atualiza um benefício existente.
	 *
	 * @param id      identificador do benefício.
	 * @param request dados de atualização.
	 * @return benefício atualizado.
	 */
	BeneficioDto atualizar(Long id, BeneficioRequestDto request);

	/**
	 * Inativa um benefício existente.
	 *
	 * @param id identificador do benefício.
	 */
	void inativar(Long id);

	/**
	 * Lista as últimas transferências registradas.
	 *
	 * @return transferências para auditoria operacional.
	 */
	List<TransferenciaDto> listarTransferencias();

	/**
	 * Transfere saldo entre dois benefícios.
	 *
	 * @param origemId      identificador do benefício debitado.
	 * @param destinoId     identificador do benefício creditado.
	 * @param valorCentavos valor da transferência em centavos.
	 * @return transferência registrada.
	 */
	TransferenciaDto transferir(Long origemId, Long destinoId, Long valorCentavos);
}
