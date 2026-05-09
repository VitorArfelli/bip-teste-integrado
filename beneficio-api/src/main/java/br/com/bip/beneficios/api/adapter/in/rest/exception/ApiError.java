package br.com.bip.beneficios.api.adapter.in.rest.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Erro padronizado retornado pela API.")
public record ApiError(
		@Schema(description = "Momento em que o erro foi gerado.", example = "2026-05-09T16:30:00Z") Instant timestamp,
		@Schema(description = "Status HTTP retornado.", example = "400") int status,
		@Schema(description = "Descrição padrão do status HTTP.", example = "Bad Request") String error,
		@Schema(description = "Código funcional ou técnico do erro.", example = "INSUFFICIENT_BALANCE") String code,
		@Schema(description = "Identificador de correlação da requisição.", example = "8f3a2e74-54b0-4c9f-8c8d-0f1e1e0d9d31") String correlationId,
		@Schema(description = "Mensagens detalhadas do erro.", example = "[\"Saldo insuficiente.\"]") List<String> messages,
		@Schema(description = "Caminho da requisição.", example = "/api/v1/beneficios/transferencias") String path) {
}
