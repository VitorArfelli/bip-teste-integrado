import { Component } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
import { BeneficiosPageComponent } from './features/beneficios/pages/beneficios-page/beneficios-page.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [BeneficiosPageComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  constructor(iconRegistry: MatIconRegistry, sanitizer: DomSanitizer) {
    Object.entries(ICONS).forEach(([name, svg]) => {
      iconRegistry.addSvgIconLiteral(name, sanitizer.bypassSecurityTrustHtml(svg));
    });
  }
}

const icon = (path: string): string => `
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  ${path}
</svg>`;

const ICONS: Record<string, string> = {
  add: icon('<path d="M12 5v14"/><path d="M5 12h14"/>'),
  arrow_forward: icon('<path d="M5 12h14"/><path d="m13 6 6 6-6 6"/>'),
  block: icon('<circle cx="12" cy="12" r="9"/><path d="m5 5 14 14"/>'),
  dashboard: icon('<rect x="3" y="3" width="7" height="8" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="15" width="7" height="6" rx="1"/>'),
  edit: icon('<path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/>'),
  filter_alt_off: icon('<path d="M3 5h14"/><path d="M6 12h6"/><path d="M10 19h1"/><path d="m3 3 18 18"/>'),
  help: icon('<circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 1 1 4.2 1.8c-.9.6-1.7 1.2-1.7 2.7"/><path d="M12 17h.01"/>'),
  inventory_2: icon('<path d="M21 8a2 2 0 0 0-1-1.7l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.7l7 4a2 2 0 0 0 2 0l7-4a2 2 0 0 0 1-1.7Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/>'),
  receipt_long: icon('<path d="M6 2h12v20l-3-2-3 2-3-2-3 2Z"/><path d="M9 7h6"/><path d="M9 11h6"/><path d="M9 15h4"/>'),
  refresh: icon('<path d="M21 12a9 9 0 0 1-15.5 6.2"/><path d="M3 12A9 9 0 0 1 18.5 5.8"/><path d="M18 2v4h4"/><path d="M6 22v-4H2"/>'),
  save: icon('<path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2Z"/><path d="M17 21v-8H7v8"/><path d="M7 3v5h8"/>'),
  search: icon('<circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/>'),
  sync_alt: icon('<path d="M7 7h11"/><path d="m14 3 4 4-4 4"/><path d="M17 17H6"/><path d="m10 13-4 4 4 4"/>'),
  warning: icon('<path d="m12 3 10 18H2Z"/><path d="M12 9v5"/><path d="M12 18h.01"/>')
};
