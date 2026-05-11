package br.com.bip.beneficios.ejb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Entidade persistente que representa um benefício e seu saldo operacional.
 *
 * <p>
 * O saldo é armazenado em centavos para manter escala monetária uniforme entre
 * banco, EJB, API e frontend.
 */
@Entity
@Table(name = "beneficio")
public class Beneficio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "nome", nullable = false, length = 100)
	private String nome;

	@Column(name = "descricao", length = 255)
	private String descricao;

	@Column(name = "valor_centavos", nullable = false)
	private Long valorCentavos;

	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	protected Beneficio() {
	}

	public Beneficio(String nome, String descricao, Long valorCentavos, boolean ativo) {
		this.nome = nome;
		this.descricao = descricao;
		this.valorCentavos = valorCentavos;
		this.ativo = ativo;
	}

	/**
	 * Atualiza os dados editáveis do benefício.
	 *
	 * @param nome          nome do benefício.
	 * @param descricao     descrição complementar.
	 * @param valorCentavos saldo em centavos.
	 * @param ativo         indica se o benefício está ativo para operações.
	 */
	public void atualizar(String nome, String descricao, Long valorCentavos, boolean ativo) {
		this.nome = nome;
		this.descricao = descricao;
		this.valorCentavos = valorCentavos;
		this.ativo = ativo;
	}

	/**
	 * Atualiza dados cadastrais sem alterar o saldo.
	 *
	 * @param nome      nome do benefício.
	 * @param descricao descrição complementar.
	 * @param ativo     indica se o benefício está ativo para operações.
	 */
	public void atualizarDados(String nome, String descricao, boolean ativo) {
		this.nome = nome;
		this.descricao = descricao;
		this.ativo = ativo;
	}

	/**
	 * Debita saldo do benefício.
	 *
	 * @param valorCentavosTransferencia valor debitado em centavos.
	 */
	public void debitar(Long valorCentavosTransferencia) {
		valorCentavos = Math.subtractExact(valorCentavos, valorCentavosTransferencia);
	}

	/**
	 * Credita saldo no benefício.
	 *
	 * @param valorCentavosTransferencia valor creditado em centavos.
	 */
	public void creditar(Long valorCentavosTransferencia) {
		valorCentavos = Math.addExact(valorCentavos, valorCentavosTransferencia);
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public Long getValorCentavos() {
		return valorCentavos;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public Long getVersion() {
		return version;
	}
}
