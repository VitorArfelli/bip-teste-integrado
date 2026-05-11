package br.com.bip.beneficios.api.adapter.in.rest;

import br.com.bip.beneficios.api.adapter.out.ejb.EjbIntegrationException;
import br.com.bip.beneficios.api.application.BeneficioService;
import br.com.bip.beneficios.api.config.CorrelationIdFilter;
import br.com.bip.beneficios.contract.dto.BeneficioDto;
import br.com.bip.beneficios.contract.dto.BeneficioRequestDto;
import br.com.bip.beneficios.contract.dto.BeneficioUpdateRequestDto;
import br.com.bip.beneficios.contract.dto.TransferenciaDto;
import br.com.bip.beneficios.contract.exception.BeneficioNotFoundException;
import br.com.bip.beneficios.contract.exception.InsufficientBalanceException;
import br.com.bip.beneficios.contract.exception.InvalidTransferenciaException;
import br.com.bip.beneficios.contract.service.BeneficioRemoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BeneficioControllerTest {
	private static final String CORRELATION_ID = "test-correlation-id";

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;
	private FakeBeneficioRemoteService remoteService;

	@BeforeEach
	void setUp() {
		remoteService = new FakeBeneficioRemoteService();
		BeneficioController controller = new BeneficioController(new BeneficioService(() -> remoteService));
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(new br.com.bip.beneficios.api.adapter.in.rest.exception.GlobalExceptionHandler())
				.addFilters(new CorrelationIdFilter())
				.setValidator(validator)
				.setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	void listaBeneficios() throws Exception {
		remoteService.beneficios.add(new BeneficioDto(1L, "Beneficio A", "Descricao A", 100000L, true, 0L));

		mockMvc.perform(get("/api/v1/beneficios").header(CorrelationIdFilter.HEADER_NAME, CORRELATION_ID))
				.andExpect(status().isOk())
				.andExpect(header().string(CorrelationIdFilter.HEADER_NAME, CORRELATION_ID))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].valorCentavos").value(100000));
	}

	@Test
	void criaBeneficio() throws Exception {
		BeneficioRequest request = new BeneficioRequest("Beneficio C", "Descricao C", 25000L, true);

		mockMvc.perform(post("/api/v1/beneficios")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.nome").value("Beneficio C"))
				.andExpect(jsonPath("$.valorCentavos").value(25000));
	}

	@Test
	void retornaBadRequestParaPayloadInvalido() throws Exception {
		mockMvc.perform(post("/api/v1/beneficios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"descricao\":\"sem nome\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("REQUEST_VALIDATION_ERROR"))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/v1/beneficios"))
				.andExpect(jsonPath("$.messages[0]").exists());
	}

	@Test
	void retornaBadRequestParaJsonMalformado() throws Exception {
		mockMvc.perform(post("/api/v1/beneficios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void retornaBadRequestParaPathVariableInvalida() throws Exception {
		mockMvc.perform(get("/api/v1/beneficios/abc"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void atualizaBeneficioSemAlterarSaldo() throws Exception {
		BeneficioUpdateRequest request = new BeneficioUpdateRequest("Beneficio Editado", "Descricao editada", true);

		mockMvc.perform(put("/api/v1/beneficios/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nome").value("Beneficio Editado"))
				.andExpect(jsonPath("$.valorCentavos").value(100000));
	}

	@Test
	void retornaNotFoundQuandoBeneficioNaoExiste() throws Exception {
		remoteService.exception = new BeneficioNotFoundException(99L);

		mockMvc.perform(get("/api/v1/beneficios/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("BENEFICIO_NOT_FOUND"))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.messages[0]", containsString("99")));
	}

	@Test
	void transfereSaldoEntreBeneficios() throws Exception {
		TransferenciaRequest request = new TransferenciaRequest(1L, 2L, 10000L);

		mockMvc.perform(post("/api/v1/beneficios/transferencias")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.origemId").value(1))
				.andExpect(jsonPath("$.destinoId").value(2))
				.andExpect(jsonPath("$.valorCentavos").value(10000));
	}

	@Test
	void retornaBadRequestQuandoTransferenciaTemPayloadInvalido() throws Exception {
		mockMvc.perform(post("/api/v1/beneficios/transferencias")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"origemId\":1,\"destinoId\":2,\"valorCentavos\":0}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("REQUEST_VALIDATION_ERROR"))
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void retornaBadRequestQuandoSaldoInsuficiente() throws Exception {
		remoteService.exception = new InsufficientBalanceException();
		TransferenciaRequest request = new TransferenciaRequest(1L, 2L, 80000L);

		mockMvc.perform(post("/api/v1/beneficios/transferencias")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INSUFFICIENT_BALANCE"))
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void retornaBadRequestQuandoTransferenciaInvalida() throws Exception {
		remoteService.exception = new InvalidTransferenciaException(
				"Beneficio de origem e destino devem ser diferentes.");
		TransferenciaRequest request = new TransferenciaRequest(1L, 1L, 10000L);

		mockMvc.perform(post("/api/v1/beneficios/transferencias")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_TRANSFERENCIA"))
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void retornaServiceUnavailableQuandoEjbFalha() throws Exception {
		remoteService.exception = new EjbIntegrationException("EJB indisponivel.",
				new RuntimeException("lookup failed"));

		mockMvc.perform(get("/api/v1/beneficios"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.code").value("EJB_INTEGRATION_ERROR"))
				.andExpect(jsonPath("$.status").value(503));
	}

	@Test
	void retornaInternalServerErrorQuandoErroInesperado() throws Exception {
		remoteService.exception = new RuntimeException("falha inesperada");

		mockMvc.perform(get("/api/v1/beneficios"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
				.andExpect(jsonPath("$.status").value(500));
	}

	private record BeneficioRequest(String nome, String descricao, Long valorCentavos, Boolean ativo) {
	}

	private record BeneficioUpdateRequest(String nome, String descricao, Boolean ativo) {
	}

	private record TransferenciaRequest(Long origemId, Long destinoId, Long valorCentavos) {
	}

	static final class FakeBeneficioRemoteService implements BeneficioRemoteService {
		private final List<BeneficioDto> beneficios = new ArrayList<>();
		private RuntimeException exception;

		@Override
		public String ping() {
			failIfConfigured();
			return "EJB OK";
		}

		@Override
		public List<BeneficioDto> listar() {
			failIfConfigured();
			return beneficios;
		}

		@Override
		public BeneficioDto buscarPorId(Long id) {
			failIfConfigured();
			return new BeneficioDto(id, "Beneficio " + id, "Descricao " + id, 100000L, true, 0L);
		}

		@Override
		public BeneficioDto criar(BeneficioRequestDto request) {
			failIfConfigured();
			return new BeneficioDto(3L, request.getNome(), request.getDescricao(), request.getValorCentavos(),
					request.getAtivo() == null || request.getAtivo(), 0L);
		}

		@Override
		public BeneficioDto atualizar(Long id, BeneficioUpdateRequestDto request) {
			failIfConfigured();
			return new BeneficioDto(id, request.getNome(), request.getDescricao(), 100000L,
					request.getAtivo() == null || request.getAtivo(), 1L);
		}

		@Override
		public void inativar(Long id) {
			failIfConfigured();
		}

		@Override
		public List<TransferenciaDto> listarTransferencias() {
			failIfConfigured();
			return List.of(new TransferenciaDto(10L, 1L, 2L, 10000L, LocalDateTime.now()));
		}

		@Override
		public TransferenciaDto transferir(Long origemId, Long destinoId, Long valorCentavos) {
			failIfConfigured();
			return new TransferenciaDto(10L, origemId, destinoId, valorCentavos, LocalDateTime.now());
		}

		private void failIfConfigured() {
			if (exception != null) {
				throw exception;
			}
		}
	}
}
