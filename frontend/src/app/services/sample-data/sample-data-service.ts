import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { Observable, firstValueFrom } from 'rxjs';

export type SampleDataResponse = {
  message: string;
};

@Injectable({
  providedIn: 'root',
})
export class SampleDataService {
  private apiUrl = 'http://localhost:8080/api/sample-data';
  private _isLoading = false;

  constructor(
    private http: HttpClient,
    private messageService: MessageService,
  ) {}

  get isLoading(): boolean {
    return this._isLoading;
  }

  generateSampleData(): Observable<SampleDataResponse> {
    return this.http.post<SampleDataResponse>(`${this.apiUrl}/generate`, {});
  }

  async generateSampleDataWithNotifications(): Promise<void> {
    this._isLoading = true;

    try {
      await firstValueFrom(this.generateSampleData());

      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Sample data generated successfully!',
        life: 5000,
      });
    } catch (error: unknown) {
      console.error('Error generating sample data', error);

      let errorMessage = 'Failed to generate sample data';

      if (error !== null && typeof error === 'object') {
        const errorObj = error as Record<string, unknown>;
        if (
          errorObj['error'] !== null &&
          errorObj['error'] !== undefined &&
          typeof errorObj['error'] === 'object'
        ) {
          const apiError = errorObj['error'] as Record<string, unknown>;
          if (typeof apiError['message'] === 'string') {
            errorMessage = apiError['message'];
          }
        }
      }

      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: errorMessage,
        life: 5000,
      });
    } finally {
      this._isLoading = false;
    }
  }
}
