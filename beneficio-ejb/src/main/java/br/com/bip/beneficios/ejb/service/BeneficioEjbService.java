package br.com.bip.beneficios.ejb.service;

import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import br.com.bip.beneficios.contract.exception.BeneficioNotFoundException;
import br.com.bip.beneficios.contract.service.BeneficioRemoteService;
import br.com.bip.beneficios.ejb.entity.Beneficio;
import br.com.bip.beneficios.ejb.entity.Transferencia;
import br.com.bip.beneficios.ejb.mapper.BeneficioMapper;
import br.com.bip.beneficios.ejb.validation.BeneficioValidator;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;

/**
 * EJB remoto responsável pelas regras transacionais de benefícios.
 *
 * <p>
 * As operações usam transação container-managed. A transferência aplica lock
 * pessimista nos benefícios envolvidos para evitar inconsistência de saldo em
 * chamadas concorrentes.
 */
@Stateless(name = "BeneficioRemoteService")
@Remote(BeneficioRemoteService.class)
public class BeneficioEjbService implements BeneficioRemoteService {
	@PersistenceContext(unitName = "beneficiosPU")
	private EntityManager entityManager;

	@Override
	public String ping() {
		return "EJB OK";
	}

	/**
	 * Lista todos os benefícios cadastrados.
	 *
	 * @return benefícios ordenados por identificador.
	 */
	@Override
	public List<BeneficioDto> listar() {
		return entityManager.createQuery("select b from Beneficio b order by b.id", Beneficio.class).getResultStream()
				.map(BeneficioMapper::toDto).toList();
	}

	/**
	 * Busca um benefício existente.
	 *
	 * @param id identificador do benefício.
	 * @return benefício encontrado.
	 */
	@Override
	public BeneficioDto buscarPorId(Long id) {
		return BeneficioMapper.toDto(buscarBeneficio(id));
	}

	/**
	 * Valida e persiste um benefício.
	 *
	 * @param request dados de criação.
	 * @return benefício criado.
	 */
	@Override
	public BeneficioDto criar(BeneficioRequestDto request) {
		BeneficioValidator.validarBeneficio(request);
		Beneficio beneficio = new Beneficio(request.getNome().trim(), request.getDescricao(),
				request.getValorCentavos(), request.getAtivo() == null || request.getAtivo());
		entityManager.persist(beneficio);
		entityManager.flush();
		return BeneficioMapper.toDto(beneficio);
	}

	/**
	 * Valida e atualiza um benefício existente.
	 *
	 * @param id      identificador do benefício.
	 * @param request dados de atualização.
	 * @return benefício atualizado.
	 */
	@Override
	public BeneficioDto atualizar(Long id, BeneficioRequestDto request) {
		BeneficioValidator.validarBeneficio(request);
		Beneficio beneficio = buscarBeneficio(id);
		beneficio.atualizar(request.getNome().trim(), request.getDescricao(), request.getValorCentavos(),
				request.getAtivo() == null || request.getAtivo());
		return BeneficioMapper.toDto(beneficio);
	}

	/**
	 * Inativa um benefício sem removê-lo do histórico.
	 *
	 * @param id identificador do benefício.
	 */
	@Override
	public void inativar(Long id) {
		Beneficio beneficio = buscarBeneficio(id);
		beneficio.atualizar(beneficio.getNome(), beneficio.getDescricao(), beneficio.getValorCentavos(), false);
	}

	/**
	 * Lista as últimas transferências registradas para auditoria.
	 *
	 * @return até cinquenta transferências recentes.
	 */
	@Override
	public List<TransferenciaDto> listarTransferencias() {
		return entityManager.createQuery("select t from Transferencia t order by t.criadaEm desc", Transferencia.class)
				.setMaxResults(50).getResultStream().map(BeneficioMapper::toDto).toList();
	}

	/**
	 * Executa transferência de saldo entre dois benefícios.
	 *
	 * <p>
	 * Os locks são adquiridos em ordem crescente de ID para reduzir risco de
	 * deadlock quando duas transferências concorrentes envolvem os mesmos saldos.
	 *
	 * @param origemId      benefício debitado.
	 * @param destinoId     benefício creditado.
	 * @param valorCentavos valor em centavos.
	 * @return transferência registrada.
	 */
	@Override
	public TransferenciaDto transferir(Long origemId, Long destinoId, Long valorCentavos) {
		BeneficioValidator.validarTransferencia(origemId, destinoId, valorCentavos);

		List<Long> idsOrdenados = List.of(origemId, destinoId).stream().sorted(Comparator.naturalOrder()).toList();
		Beneficio primeiroLock = buscarBeneficioComLock(idsOrdenados.get(0));
		Beneficio segundoLock = buscarBeneficioComLock(idsOrdenados.get(1));

		Beneficio origem = primeiroLock.getId().equals(origemId) ? primeiroLock : segundoLock;
		Beneficio destino = primeiroLock.getId().equals(destinoId) ? primeiroLock : segundoLock;

		BeneficioValidator.validarBeneficioAtivo(origem, "origem");
		BeneficioValidator.validarBeneficioAtivo(destino, "destino");
		BeneficioValidator.validarSaldo(origem, valorCentavos);

		origem.debitar(valorCentavos);
		destino.creditar(valorCentavos);

		Transferencia transferencia = new Transferencia(origem, destino, valorCentavos);
		entityManager.persist(transferencia);
		entityManager.flush();
		return BeneficioMapper.toDto(transferencia);
	}

	private Beneficio buscarBeneficio(Long id) {
		if (id == null) {
			throw new BeneficioNotFoundException(id);
		}
		Beneficio beneficio = entityManager.find(Beneficio.class, id);
		if (beneficio == null) {
			throw new BeneficioNotFoundException(id);
		}
		return beneficio;
	}

	private Beneficio buscarBeneficioComLock(Long id) {
		Beneficio beneficio = entityManager.find(Beneficio.class, id, LockModeType.PESSIMISTIC_WRITE);
		if (beneficio == null) {
			throw new BeneficioNotFoundException(id);
		}
		return beneficio;
	}
}
