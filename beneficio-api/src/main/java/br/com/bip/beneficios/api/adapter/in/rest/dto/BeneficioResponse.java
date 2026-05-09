package br.com.bip.beneficios.api.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Benefício retornado pela API.")
public class BeneficioResponse {
	@Schema(description = "Identificador do benefício.", example = "1")
	private Long id;
	@Schema(description = "Nome do benefício.", example = "Beneficio A")
	private String nome;
	@Schema(description = "Descrição complementar do benefício.", example = "Descrição A")
	private String descricao;
	@Schema(description = "Saldo do benefício em centavos. Exemplo: 100000 representa R$ 1.000,00.", example = "100000")
	private Long valorCentavos;
	@Schema(description = "Indica se o benefício está ativo para operações.", example = "true")
	private boolean ativo;
	@Schema(description = "Versão JPA usada para controle otimista.", example = "0")
	private Long version;

	public BeneficioResponse(Long id, String nome, String descricao, Long valorCentavos, boolean ativo, Long version) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.valorCentavos = valorCentavos;
		this.ativo = ativo;
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public Long getValorCentavos() {
		return valorCentavos;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public Long getVersion() {
		return version;
	}
}
