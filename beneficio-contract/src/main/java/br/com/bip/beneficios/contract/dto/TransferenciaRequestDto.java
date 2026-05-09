package br.com.bip.beneficios.contract.dto;

import java.io.Serializable;

public class TransferenciaRequestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long origemId;
	private Long destinoId;
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
