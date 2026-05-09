package br.com.bip.beneficios.api.adapter.out.ejb;

import br.com.bip.beneficios.api.application.port.BeneficioRemoteClient;
import br.com.bip.beneficios.contract.service.BeneficioRemoteService;
import java.util.Properties;
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
}
