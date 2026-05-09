package br.com.bip.beneficios.api.application;

import br.com.bip.beneficios.api.application.port.BeneficioRemoteClient;
import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import br.com.bip.beneficios.contract.dto.TransferenciaRequestDto;
import br.com.bip.beneficios.contract.service.BeneficioRemoteService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeneficioServiceTest {
    @Test
    void delegaTransferenciaParaServicoRemoto() {
        FakeBeneficioRemoteService remoteService = new FakeBeneficioRemoteService();

        TransferenciaRequestDto request = new TransferenciaRequestDto();
        request.setOrigemId(1L);
        request.setDestinoId(2L);
        request.setValorCentavos(1000L);

        new BeneficioService(new StaticBeneficioRemoteClient(remoteService)).transferir(request);

        assertEquals(1L, remoteService.origemId);
        assertEquals(2L, remoteService.destinoId);
        assertEquals(1000L, remoteService.valorCentavos);
    }

    @Test
    void delegaListagemDeTransferenciasParaServicoRemoto() {
        FakeBeneficioRemoteService remoteService = new FakeBeneficioRemoteService();
        remoteService.transferencias.add(new TransferenciaDto(10L, 1L, 2L, 1000L, null));

        List<TransferenciaDto> transferencias =
                new BeneficioService(new StaticBeneficioRemoteClient(remoteService)).listarTransferencias();

        assertEquals(1, remoteService.listarTransferenciasChamadas);
        assertEquals(10L, transferencias.get(0).getId());
    }

    private record StaticBeneficioRemoteClient(BeneficioRemoteService service) implements BeneficioRemoteClient {
    }

    private static final class FakeBeneficioRemoteService implements BeneficioRemoteService {
        private final List<TransferenciaDto> transferencias = new ArrayList<>();
        private Long origemId;
        private Long destinoId;
        private Long valorCentavos;
        private int listarTransferenciasChamadas;

        @Override
        public String ping() {
            return "EJB OK";
        }

        @Override
        public List<BeneficioDto> listar() {
            return List.of();
        }

        @Override
        public BeneficioDto buscarPorId(Long id) {
            return null;
        }

        @Override
        public BeneficioDto criar(BeneficioRequestDto request) {
            return null;
        }

        @Override
        public BeneficioDto atualizar(Long id, BeneficioRequestDto request) {
            return null;
        }

        @Override
        public void inativar(Long id) {
        }

        @Override
        public List<TransferenciaDto> listarTransferencias() {
            listarTransferenciasChamadas++;
            return transferencias;
        }

        @Override
        public TransferenciaDto transferir(Long origemId, Long destinoId, Long valorCentavos) {
            this.origemId = origemId;
            this.destinoId = destinoId;
            this.valorCentavos = valorCentavos;
            return new TransferenciaDto(1L, origemId, destinoId, valorCentavos, null);
        }
    }
}
