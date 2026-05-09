package br.com.bip.beneficios.ejb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Registro de auditoria de uma transferência de saldo entre benefícios.
 */
@Entity
@Table(name = "transferencia")
public class Transferencia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "origem_beneficio_id", nullable = false)
	private Beneficio origem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "destino_beneficio_id", nullable = false)
	private Beneficio destino;

	@Column(name = "valor_centavos", nullable = false)
	private Long valorCentavos;

	@Column(name = "criada_em", nullable = false)
	private LocalDateTime criadaEm;

	protected Transferencia() {
	}

	/**
	 * Cria o registro de transferência com data de criação local.
	 *
	 * @param origem        benefício debitado.
	 * @param destino       benefício creditado.
	 * @param valorCentavos valor transferido em centavos.
	 */
	public Transferencia(Beneficio origem, Beneficio destino, Long valorCentavos) {
		this.origem = origem;
		this.destino = destino;
		this.valorCentavos = valorCentavos;
		this.criadaEm = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Beneficio getOrigem() {
		return origem;
	}

	public Beneficio getDestino() {
		return destino;
	}

	public Long getValorCentavos() {
		return valorCentavos;
	}

	public LocalDateTime getCriadaEm() {
		return criadaEm;
	}
}
