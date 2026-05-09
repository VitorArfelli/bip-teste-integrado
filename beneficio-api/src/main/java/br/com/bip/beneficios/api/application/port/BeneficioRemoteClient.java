package br.com.bip.beneficios.api.application.port;

import br.com.bip.beneficios.contract.service.BeneficioRemoteService;

/**
 * Porta de saída usada pela aplicação para obter o serviço remoto de
 * benefícios.
 */
public interface BeneficioRemoteClient {
	/**
	 * Retorna uma referência pronta para uso do EJB remoto.
	 *
	 * @return serviço remoto de benefícios.
	 */
	BeneficioRemoteService service();
}
