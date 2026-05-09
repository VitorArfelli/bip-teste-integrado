package br.com.bip.beneficios.contract.dto;

import java.io.Serializable;

public class BeneficioRequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String nome;
	private String descricao;
	private Long valorCentavos;
	private Boolean ativo;

	public BeneficioRequestDto() {
	}

	public BeneficioRequestDto(String nome, String descricao, Long valorCentavos, Boolean ativo) {
		this.nome = nome;
		this.descricao = descricao;
		this.valorCentavos = valorCentavos;
		this.ativo = ativo;
	}

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
