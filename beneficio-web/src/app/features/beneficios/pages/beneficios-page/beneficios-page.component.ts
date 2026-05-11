import { CommonModule, CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ConfirmDialogComponent } from '../../../../shared/ui/confirm-dialog/confirm-dialog.component';
import { BeneficioFormDialogComponent } from '../../components/beneficio-form-dialog/beneficio-form-dialog.component';
import { TransferenciaDialogComponent } from '../../components/transferencia-dialog/transferencia-dialog.component';
import { ApiError, Beneficio, BeneficioCreateRequest, BeneficioUpdateRequest, Transferencia } from '../../models/beneficio.model';
import { BeneficioService } from '../../services/beneficio.service';
import { centavosParaReais } from '../../utils/money.util';

@Component({
  selector: 'app-beneficios-page',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatTooltipModule
  ],
  templateUrl: './beneficios-page.component.html',
  styleUrl: './beneficios-page.component.css'
})
export class BeneficiosPageComponent implements OnInit {
  readonly columns = ['nome', 'descricao', 'saldo', 'ativo', 'version', 'actions'];
  beneficios: Beneficio[] = [];
  transferencias: Transferencia[] = [];
  ejbStatus = 'Verificando';
  loading = false;
  mutating = false;
  filtroNome = '';
  filtroStatus: 'todos' | 'ativos' | 'inativos' = 'todos';
  ordenacaoSaldo: 'desc' | 'asc' = 'desc';

  constructor(
    private readonly beneficioService: BeneficioService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.carregar();
    this.carregarStatusEjb();
  }

  carregar(): void {
    this.loading = true;
    this.beneficioService.listar().subscribe({
      next: (beneficios) => {
        this.beneficios = beneficios;
        this.carregarTransferencias();
      },
      error: (error) => this.handleError(error)
    });
  }

  carregarStatusEjb(): void {
    this.beneficioService.healthEjb().subscribe({
      next: (health) => {
        this.ejbStatus = health.status;
      },
      error: () => {
        this.ejbStatus = 'Indisponivel';
      }
    });
  }

  carregarTransferencias(): void {
    this.beneficioService.listarTransferencias().subscribe({
      next: (transferencias) => {
        this.transferencias = transferencias;
        this.loading = false;
      },
      error: (error) => this.handleError(error)
    });
  }

  novo(): void {
    this.dialog.open(BeneficioFormDialogComponent, { width: '560px', data: null })
      .afterClosed()
      .subscribe((request) => {
        if (!request) {
          return;
        }
        this.mutating = true;
        this.beneficioService.criar(request as BeneficioCreateRequest).subscribe({
          next: () => this.afterMutation('Beneficio criado.'),
          error: (error) => this.handleError(error)
        });
      });
  }

  editar(beneficio: Beneficio): void {
    this.dialog.open(BeneficioFormDialogComponent, { width: '560px', data: beneficio })
      .afterClosed()
      .subscribe((request) => {
        if (!request) {
          return;
        }
        this.mutating = true;
        this.beneficioService.atualizar(beneficio.id, request as BeneficioUpdateRequest).subscribe({
          next: () => this.afterMutation('Beneficio atualizado.'),
          error: (error) => this.handleError(error)
        });
      });
  }

  inativar(beneficio: Beneficio): void {
    this.dialog.open(ConfirmDialogComponent, {
      width: '440px',
      maxWidth: 'calc(100vw - 32px)',
      data: {
        title: 'Inativar beneficio',
        message: `O beneficio ${beneficio.nome} deixara de participar de novas transferencias.`,
        confirmLabel: 'Inativar',
        tone: 'danger'
      }
    }).afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }
      this.mutating = true;
      this.beneficioService.inativar(beneficio.id).subscribe({
        next: () => this.afterMutation('Beneficio inativado.'),
        error: (error) => this.handleError(error)
      });
    });
  }

  abrirTransferencia(): void {
    const ativos = this.beneficios.filter((beneficio) => beneficio.ativo);
    this.dialog.open(TransferenciaDialogComponent, { width: '620px', data: { beneficios: ativos } })
      .afterClosed()
      .subscribe((request) => {
        if (!request) {
          return;
        }
        this.mutating = true;
        this.beneficioService.transferir(request).subscribe({
          next: () => {
            this.carregarStatusEjb();
            this.afterMutation('Transferencia realizada.');
          },
          error: (error) => this.handleError(error)
        });
      });
  }

  get beneficiosFiltrados(): Beneficio[] {
    const termo = this.filtroNome.trim().toLowerCase();
    return this.beneficios
      .filter((beneficio) => !termo || beneficio.nome.toLowerCase().includes(termo))
      .filter((beneficio) => {
        if (this.filtroStatus === 'ativos') {
          return beneficio.ativo;
        }
        if (this.filtroStatus === 'inativos') {
          return !beneficio.ativo;
        }
        return true;
      })
      .sort((a, b) => this.ordenacaoSaldo === 'desc'
        ? b.valorCentavos - a.valorCentavos
        : a.valorCentavos - b.valorCentavos);
  }

  get beneficiosAtivos(): number {
    return this.beneficios.filter((beneficio) => beneficio.ativo).length;
  }

  get saldoTotal(): number {
    return centavosParaReais(this.beneficios.reduce((total, beneficio) => total + beneficio.valorCentavos, 0));
  }

  get transferenciasMes(): number {
    const agora = new Date();
    return this.transferencias.filter((transferencia) => {
      const criadaEm = new Date(transferencia.criadaEm);
      return criadaEm.getMonth() === agora.getMonth() && criadaEm.getFullYear() === agora.getFullYear();
    }).length;
  }

  get ultimaTransferencia(): Transferencia | undefined {
    return this.transferencias[0];
  }

  get hasFilters(): boolean {
    return Boolean(this.filtroNome.trim()) || this.filtroStatus !== 'todos' || this.ordenacaoSaldo !== 'desc';
  }

  nomeBeneficio(id: number): string {
    return this.beneficios.find((beneficio) => beneficio.id === id)?.nome ?? `#${id}`;
  }

  formatarCentavos(valorCentavos: number): number {
    return centavosParaReais(valorCentavos);
  }

  saldoTone(beneficio: Beneficio): 'zero' | 'low' | 'normal' {
    if (beneficio.valorCentavos === 0) {
      return 'zero';
    }
    return beneficio.valorCentavos < 10000 ? 'low' : 'normal';
  }

  limparFiltros(): void {
    this.filtroNome = '';
    this.filtroStatus = 'todos';
    this.ordenacaoSaldo = 'desc';
  }

  private afterMutation(message: string): void {
    this.mutating = false;
    this.snackBar.open(message, 'OK', { duration: 2800 });
    this.carregar();
  }

  private handleError(error: HttpErrorResponse): void {
    this.loading = false;
    this.mutating = false;
    const body = error.error as ApiError | undefined;
    const message = body?.messages?.join(' ') || 'Operacao nao concluida.';
    this.snackBar.open(message, 'OK', { duration: 5000 });
  }
}
