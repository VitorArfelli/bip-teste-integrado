package br.com.bip.beneficios.api.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de benefício.")
public class BeneficioCreateRequest {
	@Schema(description = "Nome do benefício.", example = "Beneficio A", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank
	@Size(max = 100)
	private String nome;

	@Schema(description = "Descrição complementar do benefício.", example = "Descrição A", maxLength = 255)
	@Size(max = 255)
	private String descricao;

	@Schema(description = "Saldo do benefício em centavos. Exemplo: 100000 representa R$ 1.000,00.", example = "100000", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
	@NotNull
	@PositiveOrZero
	private Long valorCentavos;

	@Schema(description = "Indica se o benefício está ativo para operações.", example = "true", defaultValue = "true")
	private Boolean ativo = true;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getValorCentavos() {
		return valorCentavos;
	}

	public void setValorCentavos(Long valorCentavos) {
		this.valorCentavos = valorCentavos;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
