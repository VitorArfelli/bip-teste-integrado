package br.com.bip.beneficios.api.adapter.in.rest;

import br.com.bip.beneficios.api.adapter.in.rest.dto.BeneficioCreateRequest;
import br.com.bip.beneficios.api.adapter.in.rest.dto.BeneficioResponse;
import br.com.bip.beneficios.api.adapter.in.rest.dto.BeneficioUpdateRequest;
import br.com.bip.beneficios.api.adapter.in.rest.dto.TransferenciaRequest;
import br.com.bip.beneficios.api.adapter.in.rest.dto.TransferenciaResponse;
import br.com.bip.beneficios.api.adapter.in.rest.exception.ApiError;
import br.com.bip.beneficios.api.adapter.in.rest.mapper.BeneficioRestMapper;
import br.com.bip.beneficios.api.application.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Beneficios", description = "Operações de cadastro, consulta, inativação e transferência de benefícios.")
public class BeneficioController {
	private final BeneficioService beneficioService;

	public BeneficioController(BeneficioService beneficioService) {
		this.beneficioService = beneficioService;
	}

	@GetMapping
	@Operation(summary = "Lista benefícios", description = "Retorna todos os benefícios cadastrados, ordenados por identificador.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Benefícios listados com sucesso.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BeneficioResponse.class)))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public List<BeneficioResponse> listar() {
		return beneficioService.listar().stream().map(BeneficioRestMapper::toResponse).toList();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Busca benefício por ID", description = "Retorna os dados de um benefício específico.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Benefício encontrado.", content = @Content(schema = @Schema(implementation = BeneficioResponse.class))),
			@ApiResponse(responseCode = "404", description = "Benefício não encontrado.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public BeneficioResponse buscarPorId(
			@Parameter(description = "Identificador do benefício.", example = "1") @PathVariable("id") Long id) {
		return BeneficioRestMapper.toResponse(beneficioService.buscarPorId(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Cria benefício", description = "Cadastra um benefício com saldo inicial informado em centavos.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Benefício criado com sucesso.", content = @Content(schema = @Schema(implementation = BeneficioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public BeneficioResponse criar(@Valid @RequestBody BeneficioCreateRequest request) {
		return BeneficioRestMapper.toResponse(beneficioService.criar(BeneficioRestMapper.toContract(request)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualiza benefício", description = "Atualiza dados cadastrais, status e saldo do benefício.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso.", content = @Content(schema = @Schema(implementation = BeneficioResponse.class))),
			@ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "404", description = "Benefício não encontrado.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public BeneficioResponse atualizar(
			@Parameter(description = "Identificador do benefício.", example = "1") @PathVariable("id") Long id,
			@Valid @RequestBody BeneficioUpdateRequest request) {
		return BeneficioRestMapper.toResponse(beneficioService.atualizar(id, BeneficioRestMapper.toContract(request)));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Inativa benefício", description = "Marca o benefício como inativo. Benefícios inativos não participam de transferências.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Benefício inativado com sucesso.", content = @Content),
			@ApiResponse(responseCode = "404", description = "Benefício não encontrado.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public void inativar(
			@Parameter(description = "Identificador do benefício.", example = "1") @PathVariable("id") Long id) {
		beneficioService.inativar(id);
	}

	@GetMapping("/transferencias")
	@Operation(summary = "Lista transferências", description = "Retorna as últimas transferências registradas para auditoria operacional.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Transferências listadas com sucesso.", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransferenciaResponse.class)))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public List<TransferenciaResponse> listarTransferencias() {
		return beneficioService.listarTransferencias().stream().map(BeneficioRestMapper::toResponse).toList();
	}

	@PostMapping("/transferencias")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Transfere saldo entre benefícios", description = "Executa transferência com validações de negócio, transação no EJB, lock pessimista e auditoria.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso.", content = @Content(schema = @Schema(implementation = TransferenciaResponse.class))),
			@ApiResponse(responseCode = "400", description = "Payload inválido, origem igual ao destino, benefício inativo ou saldo insuficiente.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "404", description = "Benefício de origem ou destino não encontrado.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "503", description = "Falha de comunicação com o EJB remoto.", content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno inesperado.", content = @Content(schema = @Schema(implementation = ApiError.class))) })
	public TransferenciaResponse transferir(@Valid @RequestBody TransferenciaRequest request) {
		return BeneficioRestMapper.toResponse(beneficioService.transferir(BeneficioRestMapper.toContract(request)));
	}
}
