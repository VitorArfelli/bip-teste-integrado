package br.com.bip.beneficios.ejb.validation;

import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.exception.InactiveBeneficioException;
import br.com.bip.beneficios.contract.exception.InsufficientBalanceException;
import br.com.bip.beneficios.contract.exception.InvalidBeneficioException;
import br.com.bip.beneficios.contract.exception.InvalidTransferenciaException;
import br.com.bip.beneficios.ejb.entity.Beneficio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeneficioValidatorTest {
    @Test
    void rejeitaBeneficioSemDados() {
        InvalidBeneficioException exception =
                assertThrows(InvalidBeneficioException.class, () -> BeneficioValidator.validarBeneficio(null));

        assertEquals("INVALID_BENEFICIO", exception.getCode());
    }

    @Test
    void rejeitaBeneficioSemNome() {
        BeneficioRequestDto request = new BeneficioRequestDto(" ", "vale refeicao", 1000L, true);

        InvalidBeneficioException exception = assertThrows(InvalidBeneficioException.class,
                () -> BeneficioValidator.validarBeneficio(request));

        assertEquals("INVALID_BENEFICIO", exception.getCode());
    }

    @Test
    void rejeitaBeneficioComValorNegativo() {
        BeneficioRequestDto request = new BeneficioRequestDto("VR", "vale refeicao", -1L, true);

        InvalidBeneficioException exception = assertThrows(InvalidBeneficioException.class,
                () -> BeneficioValidator.validarBeneficio(request));

        assertEquals("INVALID_BENEFICIO", exception.getCode());
    }

    @Test
    void aceitaBeneficioValido() {
        BeneficioRequestDto request = new BeneficioRequestDto("VR", "vale refeicao", 1000L, true);

        assertDoesNotThrow(() -> BeneficioValidator.validarBeneficio(request));
    }

    @Test
    void rejeitaTransferenciaSemOrigem() {
        InvalidTransferenciaException exception = assertThrows(InvalidTransferenciaException.class,
                () -> BeneficioValidator.validarTransferencia(null, 2L, 1000L));

        assertEquals("INVALID_TRANSFERENCIA", exception.getCode());
    }

    @Test
    void rejeitaTransferenciaSemDestino() {
        InvalidTransferenciaException exception = assertThrows(InvalidTransferenciaException.class,
                () -> BeneficioValidator.validarTransferencia(1L, null, 1000L));

        assertEquals("INVALID_TRANSFERENCIA", exception.getCode());
    }

    @Test
    void rejeitaTransferenciaComValorZero() {
        InvalidTransferenciaException exception = assertThrows(InvalidTransferenciaException.class,
                () -> BeneficioValidator.validarTransferencia(1L, 2L, 0L));

        assertEquals("INVALID_TRANSFERENCIA", exception.getCode());
    }

    @Test
    void rejeitaTransferenciaComValorNegativo() {
        InvalidTransferenciaException exception = assertThrows(InvalidTransferenciaException.class,
                () -> BeneficioValidator.validarTransferencia(1L, 2L, -1L));

        assertEquals("INVALID_TRANSFERENCIA", exception.getCode());
    }

    @Test
    void rejeitaTransferenciaParaMesmoBeneficio() {
        InvalidTransferenciaException exception = assertThrows(InvalidTransferenciaException.class,
                () -> BeneficioValidator.validarTransferencia(1L, 1L, 1000L));

        assertEquals("INVALID_TRANSFERENCIA", exception.getCode());
    }

    @Test
    void aceitaTransferenciaValida() {
        assertDoesNotThrow(() -> BeneficioValidator.validarTransferencia(1L, 2L, 1000L));
    }

    @Test
    void rejeitaBeneficioInativoNaTransferencia() {
        Beneficio beneficio = new Beneficio("VR", "vale refeicao", 1000L, false);

        InactiveBeneficioException exception = assertThrows(InactiveBeneficioException.class,
                () -> BeneficioValidator.validarBeneficioAtivo(beneficio, "origem"));

        assertEquals("BENEFICIO_INACTIVE", exception.getCode());
    }

    @Test
    void rejeitaTransferenciaSemSaldoSuficiente() {
        Beneficio origem = new Beneficio("VR", "vale refeicao", 999L, true);

        InsufficientBalanceException exception =
                assertThrows(InsufficientBalanceException.class, () -> BeneficioValidator.validarSaldo(origem, 1000L));

        assertEquals("INSUFFICIENT_BALANCE", exception.getCode());
    }
}
