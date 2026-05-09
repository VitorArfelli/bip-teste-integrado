import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Beneficio, BeneficioRequest, EjbHealth, Transferencia, TransferenciaRequest } from '../models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficioService {
  private readonly baseUrl = '/api/v1/beneficios';
  private readonly healthUrl = '/api/v1/health/ejb';

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.baseUrl);
  }

  criar(request: BeneficioRequest): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.baseUrl, request);
  }

  atualizar(id: number, request: BeneficioRequest): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, request);
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  listarTransferencias(): Observable<Transferencia[]> {
    return this.http.get<Transferencia[]>(`${this.baseUrl}/transferencias`);
  }

  transferir(request: TransferenciaRequest): Observable<Transferencia> {
    return this.http.post<Transferencia>(`${this.baseUrl}/transferencias`, request);
  }

  healthEjb(): Observable<EjbHealth> {
    return this.http.get<EjbHealth>(this.healthUrl);
  }
}
