package br.com.bip.beneficios.api.adapter.in.rest;

import br.com.bip.beneficios.api.application.BeneficioService;
import br.com.bip.beneficios.api.adapter.in.rest.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Endpoints de verificação da aplicação e integração EJB.")
public class HealthController {
	private final BeneficioService beneficioService;

	public HealthController(BeneficioService beneficioService) {
		this.beneficioService = beneficioService;
	}

	@GetMapping("/ejb")
	@Operation(summary = "Verifica integração com EJB", description = "Executa chamada simples ao EJB remoto para validar conectividade via JNDI.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "EJB remoto respondeu com sucesso.", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public Map<String, String> pingEjb() {
		return Map.of("status", beneficioService.pingEjb());
	}
}
