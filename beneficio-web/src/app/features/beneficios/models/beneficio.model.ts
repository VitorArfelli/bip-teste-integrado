export interface Beneficio {
  id: number;
  nome: string;
  descricao?: string;
  valorCentavos: number;
  ativo: boolean;
  version: number;
}

export interface BeneficioCreateRequest {
  nome: string;
  descricao?: string;
  valorCentavos: number;
  ativo: boolean;
}

export interface BeneficioUpdateRequest {
  nome: string;
  descricao?: string;
  ativo: boolean;
}

export interface TransferenciaRequest {
  origemId: number;
  destinoId: number;
  valorCentavos: number;
}

export interface Transferencia {
  id: number;
  origemId: number;
  destinoId: number;
  valorCentavos: number;
  criadaEm: string;
}

export interface ApiError {
  messages: string[];
}

export interface EjbHealth {
  status: string;
}
