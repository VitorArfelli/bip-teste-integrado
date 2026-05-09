import { Component } from '@angular/core';
import { BeneficiosPageComponent } from './features/beneficios/pages/beneficios-page/beneficios-page.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [BeneficiosPageComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
}
