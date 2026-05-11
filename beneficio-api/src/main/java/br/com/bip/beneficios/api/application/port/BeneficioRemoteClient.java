package br.com.bip.beneficios.api.application.port;

import br.com.bip.beneficios.contract.service.BeneficioRemoteService;
import java.util.function.Consumer;
import java.util.function.Function;

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

	default <T> T call(Function<BeneficioRemoteService, T> operation) {
		return operation.apply(service());
	}

	default void run(Consumer<BeneficioRemoteService> operation) {
		operation.accept(service());
	}
}
