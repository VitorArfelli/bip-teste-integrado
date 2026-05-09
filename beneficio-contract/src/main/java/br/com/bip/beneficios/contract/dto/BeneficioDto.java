package br.com.bip.beneficios.contract.dto;

import java.io.Serializable;

public class BeneficioDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String nome;
	private String descricao;
	private Long valorCentavos;
	private boolean ativo;
	private Long version;

	public BeneficioDto() {
	}

	public BeneficioDto(Long id, String nome, String descricao, Long valorCentavos, boolean ativo, Long version) {
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

	public void setId(Long id) {
		this.id = id;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
