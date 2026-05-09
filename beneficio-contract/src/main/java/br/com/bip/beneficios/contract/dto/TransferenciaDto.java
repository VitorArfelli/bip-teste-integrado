package br.com.bip.beneficios.contract.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TransferenciaDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long origemId;
	private Long destinoId;
	private Long valorCentavos;
	private LocalDateTime criadaEm;

	public TransferenciaDto() {
	}

	public TransferenciaDto(Long id, Long origemId, Long destinoId, Long valorCentavos, LocalDateTime criadaEm) {
		this.id = id;
		this.origemId = origemId;
		this.destinoId = destinoId;
		this.valorCentavos = valorCentavos;
		this.criadaEm = criadaEm;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}

	public void setCriadaEm(LocalDateTime criadaEm) {
		this.criadaEm = criadaEm;
	}
}
