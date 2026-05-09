package br.com.bip.beneficios.ejb.mapper;

import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import br.com.bip.beneficios.ejb.entity.Beneficio;
import br.com.bip.beneficios.ejb.entity.Transferencia;

public final class BeneficioMapper {
	private BeneficioMapper() {
	}

	public static BeneficioDto toDto(Beneficio beneficio) {
		return new BeneficioDto(beneficio.getId(), beneficio.getNome(), beneficio.getDescricao(),
				beneficio.getValorCentavos(), beneficio.isAtivo(), beneficio.getVersion());
	}

	public static TransferenciaDto toDto(Transferencia transferencia) {
		return new TransferenciaDto(transferencia.getId(), transferencia.getOrigem().getId(),
				transferencia.getDestino().getId(), transferencia.getValorCentavos(), transferencia.getCriadaEm());
	}
}
