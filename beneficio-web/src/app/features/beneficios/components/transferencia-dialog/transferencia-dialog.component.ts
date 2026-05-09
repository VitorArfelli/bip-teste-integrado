import { Component, Inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Beneficio, TransferenciaRequest } from '../../models/beneficio.model';
import { centavosParaReais, reaisParaCentavos } from '../../utils/money.util';
import { CurrencyMaskDirective } from '../../../../shared/directives/currency-mask.directive';

@Component({
  selector: 'app-transferencia-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CurrencyPipe,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    CurrencyMaskDirective
  ],
  templateUrl: './transferencia-dialog.component.html',
  styleUrl: './transferencia-dialog.component.css'
})
export class TransferenciaDialogComponent {
  readonly form = this.formBuilder.nonNullable.group({
    origemId: [this.data.beneficios[0]?.id ?? 0, [Validators.required, Validators.min(1)]],
    destinoId: [this.data.beneficios[1]?.id ?? 0, [Validators.required, Validators.min(1)]],
    valorReais: ['', [Validators.required]]
  });

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly dialogRef: MatDialogRef<TransferenciaDialogComponent, TransferenciaRequest>,
    @Inject(MAT_DIALOG_DATA) readonly data: { beneficios: Beneficio[] }
  ) {}

  transferir(): void {
    const valorCentavos = this.valorCentavos;
    if (!Number.isSafeInteger(valorCentavos) || valorCentavos <= 0) {
      this.form.controls.valorReais.setErrors({ valorInvalido: true });
    }
    if (this.form.invalid || this.form.value.origemId === this.form.value.destinoId || this.saldoOrigemPrevisto < 0) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close({
      origemId: this.form.controls.origemId.value,
      destinoId: this.form.controls.destinoId.value,
      valorCentavos
    });
  }

  get origem(): Beneficio | undefined {
    return this.data.beneficios.find((beneficio) => beneficio.id === this.form.value.origemId);
  }

  get destino(): Beneficio | undefined {
    return this.data.beneficios.find((beneficio) => beneficio.id === this.form.value.destinoId);
  }

  get valorCentavos(): number {
    return reaisParaCentavos(this.form.controls.valorReais.value);
  }

  get saldoOrigemPrevisto(): number {
    return (this.origem?.valorCentavos ?? 0) - (Number.isNaN(this.valorCentavos) ? 0 : this.valorCentavos);
  }

  get saldoDestinoPrevisto(): number {
    return (this.destino?.valorCentavos ?? 0) + (Number.isNaN(this.valorCentavos) ? 0 : this.valorCentavos);
  }

  formatarCentavos(valorCentavos: number | null | undefined): number {
    return centavosParaReais(valorCentavos);
  }
}
