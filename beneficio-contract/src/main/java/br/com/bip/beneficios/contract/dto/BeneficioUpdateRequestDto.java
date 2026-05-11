package br.com.bip.beneficios.contract.dto;

import java.io.Serializable;

public class BeneficioUpdateRequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String nome;
	private String descricao;
	private Boolean ativo;

	public BeneficioUpdateRequestDto() {
	}

	public BeneficioUpdateRequestDto(String nome, String descricao, Boolean ativo) {
		this.nome = nome;
		this.descricao = descricao;
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}
