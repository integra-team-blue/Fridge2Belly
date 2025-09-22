import {
  ApplicationConfig,
  provideZoneChangeDetection,
  provideHttpClient,
  provideRouter,
  provideClientHydration,
  provideProtractorTestingSupport,
  provideBrowserGlobalErrorListeners
} from '@angular/core';

import { routes } from './app.routes';
import { providePrimeNG } from 'primeng/config';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ConfirmationService, MessageService } from 'primeng/api';

import LaraLightBlue from '@primeng/themes/aura';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    providePrimeNG({
      theme: {
        preset: LaraLightBlue,
        options: {
          darkModeSelector: null,
        },
      },
    }),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    provideAnimations(),
    MessageService,
    ConfirmationService,
  ],
};
