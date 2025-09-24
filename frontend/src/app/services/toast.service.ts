import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({ providedIn: 'root' })
export class ToastService {
  constructor(private messageService: MessageService) {}

  push(message: string, severity: 'success' | 'info' | 'warn' | 'error' = 'info') {
    this.messageService.add({
      severity,
      summary: severity.charAt(0).toUpperCase() + severity.slice(1),
      detail: message,
    });
  }

  clear() {
    this.messageService.clear();
  }
}
