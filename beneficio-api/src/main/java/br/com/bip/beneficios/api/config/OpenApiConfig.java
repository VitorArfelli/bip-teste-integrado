package br.com.bip.beneficios.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "BenefitOps API", version = "v1", description = "API REST para gestão de benefícios, saldos e transferências com integração EJB."))
public class OpenApiConfig {
}
