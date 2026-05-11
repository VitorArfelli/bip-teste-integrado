package br.com.bip.beneficios.api.adapter.out.ejb;

import br.com.bip.beneficios.api.application.port.BeneficioRemoteClient;
import br.com.bip.beneficios.contract.exception.BeneficioContractException;
import br.com.bip.beneficios.contract.service.BeneficioRemoteService;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JndiBeneficioRemoteClient implements BeneficioRemoteClient {
	private final String providerUrl;
	private final String jndiName;
	private final String username;
	private final String password;
	private volatile BeneficioRemoteService service;

	public JndiBeneficioRemoteClient(@Value("${beneficios.ejb.provider-url}") String providerUrl,
			@Value("${beneficios.ejb.jndi-name}") String jndiName, @Value("${beneficios.ejb.username}") String username,
			@Value("${beneficios.ejb.password}") String password) {
		this.providerUrl = providerUrl;
		this.jndiName = jndiName;
		this.username = username;
		this.password = password;
	}

	@Override
	public BeneficioRemoteService service() {
		BeneficioRemoteService currentService = service;
		if (currentService == null) {
			synchronized (this) {
				currentService = service;
				if (currentService == null) {
					currentService = lookup();
					service = currentService;
				}
			}
		}
		return currentService;
	}

	@Override
	public <T> T call(Function<BeneficioRemoteService, T> operation) {
		try {
			return operation.apply(service());
		} catch (RuntimeException exception) {
			handleRemoteFailure(exception);
			throw exception;
		}
	}

	@Override
	public void run(Consumer<BeneficioRemoteService> operation) {
		try {
			operation.accept(service());
		} catch (RuntimeException exception) {
			handleRemoteFailure(exception);
		}
	}

	private BeneficioRemoteService lookup() {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		properties.put(Context.PROVIDER_URL, providerUrl);
		properties.put(Context.SECURITY_PRINCIPAL, username);
		properties.put(Context.SECURITY_CREDENTIALS, password);
		try {
			Object remote = new InitialContext(properties).lookup(jndiName);
			return BeneficioRemoteService.class.cast(remote);
		} catch (NamingException exception) {
			throw new EjbIntegrationException("Nao foi possivel localizar o EJB remoto.", exception);
		}
	}

	private void handleRemoteFailure(RuntimeException exception) {
		if (exception instanceof EjbIntegrationException || findContractException(exception) != null) {
			throw exception;
		}
		service = null;
		throw new EjbIntegrationException("Falha de comunicacao com o EJB remoto.", exception);
	}

	private BeneficioContractException findContractException(Throwable exception) {
		Throwable current = exception;
		while (current != null) {
			if (current instanceof BeneficioContractException contractException) {
				return contractException;
			}
			current = current.getCause();
		}
		return null;
	}
}
