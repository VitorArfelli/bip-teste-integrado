package br.com.bip.beneficios.api.application;

import br.com.bip.beneficios.api.application.port.BeneficioRemoteClient;
import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.BeneficioUpdateRequestDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import br.com.bip.beneficios.contract.dto.TransferenciaRequestDto;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Camada de aplicação da API REST.
 *
 * <p>
 * Centraliza a orquestração das chamadas HTTP e delega as regras transacionais
 * para o EJB remoto por meio de uma porta de saída.
 */
@Service
public class BeneficioService {
	private final BeneficioRemoteClient remoteClient;

	public BeneficioService(BeneficioRemoteClient remoteClient) {
		this.remoteClient = remoteClient;
	}

	/**
	 * Valida a conectividade com o EJB remoto.
	 *
	 * @return resposta de disponibilidade do EJB.
	 */
	public String pingEjb() {
		return remoteClient.service().ping();
	}

	/**
	 * Lista benefícios pela camada remota.
	 *
	 * @return benefícios cadastrados.
	 */
	public List<BeneficioDto> listar() {
		return remoteClient.service().listar();
	}

	/**
	 * Busca benefício pela camada remota.
	 *
	 * @param id identificador do benefício.
	 * @return benefício encontrado.
	 */
	public BeneficioDto buscarPorId(Long id) {
		return remoteClient.service().buscarPorId(id);
	}

	/**
	 * Cria benefício pela camada remota.
	 *
	 * @param request dados de criação.
	 * @return benefício criado.
	 */
	public BeneficioDto criar(BeneficioRequestDto request) {
		return remoteClient.service().criar(request);
	}

	/**
	 * Atualiza benefício pela camada remota.
	 *
	 * @param id      identificador do benefício.
	 * @param request dados de atualização.
	 * @return benefício atualizado.
	 */
	public BeneficioDto atualizar(Long id, BeneficioUpdateRequestDto request) {
		return remoteClient.service().atualizar(id, request);
	}

	/**
	 * Inativa benefício pela camada remota.
	 *
	 * @param id identificador do benefício.
	 */
	public void inativar(Long id) {
		remoteClient.service().inativar(id);
	}

	/**
	 * Lista transferências registradas pela camada remota.
	 *
	 * @return transferências recentes.
	 */
	public List<TransferenciaDto> listarTransferencias() {
		return remoteClient.service().listarTransferencias();
	}

	/**
	 * Solicita transferência ao EJB remoto.
	 *
	 * @param request dados da transferência.
	 * @return transferência registrada.
	 */
	public TransferenciaDto transferir(TransferenciaRequestDto request) {
		return remoteClient.service().transferir(request.getOrigemId(), request.getDestinoId(),
				request.getValorCentavos());
	}
}
