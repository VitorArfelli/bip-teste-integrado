import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { BeneficioService } from './beneficio.service';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(BeneficioService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('lista beneficios pela API', () => {
    service.listar().subscribe((beneficios) => {
      expect(beneficios.length).toBe(1);
      expect(beneficios[0].nome).toBe('Beneficio A');
    });

    const request = http.expectOne('/api/v1/beneficios');
    expect(request.request.method).toBe('GET');
    request.flush([{ id: 1, nome: 'Beneficio A', valorCentavos: 10000, ativo: true, version: 0 }]);
  });

  it('lista transferencias pela API', () => {
    service.listarTransferencias().subscribe((transferencias) => {
      expect(transferencias.length).toBe(1);
      expect(transferencias[0].valorCentavos).toBe(5000);
    });

    const request = http.expectOne('/api/v1/beneficios/transferencias');
    expect(request.request.method).toBe('GET');
    request.flush([{ id: 1, origemId: 1, destinoId: 2, valorCentavos: 5000, criadaEm: '2026-05-07T10:00:00' }]);
  });

  it('consulta status da integracao EJB', () => {
    service.healthEjb().subscribe((health) => {
      expect(health.status).toBe('EJB OK');
    });

    const request = http.expectOne('/api/v1/health/ejb');
    expect(request.request.method).toBe('GET');
    request.flush({ status: 'EJB OK' });
  });
});
