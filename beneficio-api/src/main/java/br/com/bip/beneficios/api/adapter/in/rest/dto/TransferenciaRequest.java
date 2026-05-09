package br.com.bip.beneficios.api.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Dados para transferência de saldo entre benefícios.")
public class TransferenciaRequest {
	@Schema(description = "Identificador do benefício de origem.", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Long origemId;

	@Schema(description = "Identificador do benefício de destino.", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Long destinoId;

	@Schema(description = "Valor da transferência em centavos. Exemplo: 10000 representa R$ 100,00.", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
	@NotNull
	@Positive
	private Long valorCentavos;

	public Long getOrigemId() {
		return origemId;
	}

	public void setOrigemId(Long origemId) {
		this.origemId = origemId;
	}

	public Long getDestinoId() {
		return destinoId;
	}

	public void setDestinoId(Long destinoId) {
		this.destinoId = destinoId;
	}

	public Long getValorCentavos() {
		return valorCentavos;
	}

	public void setValorCentavos(Long valorCentavos) {
		this.valorCentavos = valorCentavos;
	}
}
