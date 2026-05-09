import { Directive, ElementRef, HostListener, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { formatarMoeda } from '../../features/beneficios/utils/money.util';

@Directive({
  selector: '[appCurrencyMask]',
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CurrencyMaskDirective),
      multi: true
    }
  ]
})
export class CurrencyMaskDirective implements ControlValueAccessor {
  private onChange: (value: string) => void = () => { };
  private onTouched: () => void = () => { };

  constructor(private el: ElementRef<HTMLInputElement>) { }

  @HostListener('input', ['$event.target.value'])
  onInput(value: string): void {
    const numericValue = value.replace(/\D/g, '');
    if (!numericValue) {
      this.updateValue('');
      return;
    }

    const valueAsNumber = parseInt(numericValue, 10) / 100;
    const formattedValue = formatarMoeda(valueAsNumber);

    this.updateValue(formattedValue);
  }

  @HostListener('blur')
  onBlur(): void {
    this.onTouched();
  }

  writeValue(value: any): void {
    if (value === null || value === undefined || value === '') {
      this.el.nativeElement.value = '';
      return;
    }

    const formatted = formatarMoeda(value);
    this.el.nativeElement.value = formatted;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.el.nativeElement.disabled = isDisabled;
  }

  private updateValue(value: string): void {
    this.el.nativeElement.value = value;
    this.onChange(value);
  }
}
