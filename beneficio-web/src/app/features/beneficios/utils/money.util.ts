export function centavosParaReais(valorCentavos: number | null | undefined): number {
  return (valorCentavos ?? 0) / 100;
}

export function reaisParaCentavos(valor: string | number | null | undefined): number {
  if (typeof valor === 'number') {
    return Math.round(valor * 100);
  }

  const texto = String(valor ?? '').trim().replace(/[^\d,.]/g, '');
  if (!texto) {
    return Number.NaN;
  }

  const normalizado = normalizarSeparadorDecimal(texto);
  const valorNumerico = parseFloat(normalizado);

  if (Number.isNaN(valorNumerico)) {
    return Number.NaN;
  }

  const total = Math.round(valorNumerico * 100);
  return Number.isSafeInteger(total) ? total : Number.NaN;
}

export function centavosParaInput(valorCentavos: number | null | undefined): string {
  return centavosParaReais(valorCentavos).toFixed(2).replace('.', ',');
}

function normalizarSeparadorDecimal(valor: string): string {
  if (valor.includes(',')) {
    return valor.replace(/\./g, '').replace(',', '.');
  }

  const partes = valor.split('.');
  if (partes.length > 2) {
    return valor.replace(/\./g, '');
  }

  return valor;
}

export function formatarMoeda(valor: string | number | null | undefined): string {
  const centavos = typeof valor === 'number' ? Math.round(valor * 100) : reaisParaCentavos(valor);
  if (Number.isNaN(centavos)) {
    return '';
  }

  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(centavos / 100);
}
