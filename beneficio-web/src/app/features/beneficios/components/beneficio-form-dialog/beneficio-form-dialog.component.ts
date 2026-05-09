import { Component, Inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Beneficio, BeneficioRequest } from '../../models/beneficio.model';
import { centavosParaInput, reaisParaCentavos } from '../../utils/money.util';
import { CurrencyMaskDirective } from '../../../../shared/directives/currency-mask.directive';

@Component({
  selector: 'app-beneficio-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    CurrencyMaskDirective
  ],
  templateUrl: './beneficio-form-dialog.component.html',
  styleUrl: './beneficio-form-dialog.component.css'
})
export class BeneficioFormDialogComponent {
  readonly form = this.formBuilder.nonNullable.group({
    nome: [this.data?.nome ?? '', [Validators.required, Validators.maxLength(100)]],
    descricao: [this.data?.descricao ?? '', [Validators.maxLength(255)]],
    valorReais: [centavosParaInput(this.data?.valorCentavos), [Validators.required]],
    ativo: [this.data?.ativo ?? true]
  });

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly dialogRef: MatDialogRef<BeneficioFormDialogComponent, BeneficioRequest>,
    @Inject(MAT_DIALOG_DATA) readonly data: Beneficio | null
  ) {}

  salvar(): void {
    const valorCentavos = reaisParaCentavos(this.form.controls.valorReais.value);
    if (!Number.isSafeInteger(valorCentavos) || valorCentavos < 0) {
      this.form.controls.valorReais.setErrors({ valorInvalido: true });
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const formValue = this.form.getRawValue();
    this.dialogRef.close({
      nome: formValue.nome,
      descricao: formValue.descricao,
      valorCentavos,
      ativo: formValue.ativo
    });
  }
}
