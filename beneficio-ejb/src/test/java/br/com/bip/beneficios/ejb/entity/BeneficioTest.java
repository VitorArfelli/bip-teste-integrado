package br.com.bip.beneficios.ejb.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeneficioTest {
    @Test
    void debitaValorDoSaldo() {
        Beneficio beneficio = new Beneficio("VR", "vale refeicao", 1000L, true);

        beneficio.debitar(250L);

        assertEquals(750L, beneficio.getValorCentavos());
    }

    @Test
    void creditaValorNoSaldo() {
        Beneficio beneficio = new Beneficio("VR", "vale refeicao", 1000L, true);

        beneficio.creditar(250L);

        assertEquals(1250L, beneficio.getValorCentavos());
    }

    @Test
    void protegeContraOverflowAoCreditar() {
        Beneficio beneficio = new Beneficio("VR", "vale refeicao", Long.MAX_VALUE, true);

        assertThrows(ArithmeticException.class, () -> beneficio.creditar(1L));
    }
}
