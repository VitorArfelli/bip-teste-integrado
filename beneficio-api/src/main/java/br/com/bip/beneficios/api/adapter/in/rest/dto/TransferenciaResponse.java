package br.com.bip.beneficios.api.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Transferência registrada para auditoria.")
public class TransferenciaResponse {
	@Schema(description = "Identificador da transferência.", example = "1")
	private Long id;
	@Schema(description = "Identificador do benefício de origem.", example = "1")
	private Long origemId;
	@Schema(description = "Identificador do benefício de destino.", example = "2")
	private Long destinoId;
	@Schema(description = "Valor transferido em centavos.", example = "10000")
	private Long valorCentavos;
	@Schema(description = "Data e hora de criação da transferência.", example = "2026-05-09T16:30:00")
	private LocalDateTime criadaEm;

	public TransferenciaResponse(Long id, Long origemId, Long destinoId, Long valorCentavos, LocalDateTime criadaEm) {
		this.id = id;
		this.origemId = origemId;
		this.destinoId = destinoId;
		this.valorCentavos = valorCentavos;
		this.criadaEm = criadaEm;
	}

	public Long getId() {
		return id;
	}

	public Long getOrigemId() {
		return origemId;
	}

	public Long getDestinoId() {
		return destinoId;
	}

	public Long getValorCentavos() {
		return valorCentavos;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}
}
