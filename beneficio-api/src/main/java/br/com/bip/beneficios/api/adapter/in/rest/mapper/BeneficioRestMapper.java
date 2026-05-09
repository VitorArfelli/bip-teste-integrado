package br.com.bip.beneficios.api.adapter.in.rest.mapper;

import br.com.bip.beneficios.api.adapter.in.rest.dto.BeneficioCreateRequest;
import br.com.bip.beneficios.api.adapter.in.rest.dto.BeneficioResponse;
import br.com.bip.beneficios.api.adapter.in.rest.dto.TransferenciaRequest;
import br.com.bip.beneficios.api.adapter.in.rest.dto.TransferenciaResponse;
import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import br.com.bip.beneficios.contract.dto.TransferenciaRequestDto;

public final class BeneficioRestMapper {
	private BeneficioRestMapper() {
	}

	public static BeneficioRequestDto toContract(BeneficioCreateRequest request) {
		return new BeneficioRequestDto(request.getNome(), request.getDescricao(), request.getValorCentavos(),
				request.getAtivo());
	}

	public static TransferenciaRequestDto toContract(TransferenciaRequest request) {
		TransferenciaRequestDto dto = new TransferenciaRequestDto();
		dto.setOrigemId(request.getOrigemId());
		dto.setDestinoId(request.getDestinoId());
		dto.setValorCentavos(request.getValorCentavos());
		return dto;
	}

	public static BeneficioResponse toResponse(BeneficioDto dto) {
		return new BeneficioResponse(dto.getId(), dto.getNome(), dto.getDescricao(), dto.getValorCentavos(),
				dto.isAtivo(), dto.getVersion());
	}

	public static TransferenciaResponse toResponse(TransferenciaDto dto) {
		return new TransferenciaResponse(dto.getId(), dto.getOrigemId(), dto.getDestinoId(), dto.getValorCentavos(),
				dto.getCriadaEm());
	}
}
