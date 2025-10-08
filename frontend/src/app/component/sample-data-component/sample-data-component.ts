import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { firstValueFrom } from 'rxjs';
import { SampleDataService } from '../../services/sample-data/sample-data-service';

@Component({
  selector: 'app-sample-data-component',
  standalone: true,
  imports: [CommonModule, ToastModule, ButtonModule],
  providers: [MessageService],
  templateUrl: './sample-data-component.html',
  styleUrls: ['./sample-data-component.css'],
})
export class SampleDataComponent implements OnInit {
  isLoading = false;
  lastGenerated: Date | null = null;

  constructor(
    private sampleDataService: SampleDataService,
    private messageService: MessageService,
  ) {}

  ngOnInit() {}

  async generateSampleData() {
    this.isLoading = true;

    try {
      const response = await firstValueFrom(this.sampleDataService.generateSampleData());

      this.lastGenerated = new Date();

      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: response.message ?? 'Sample data generated successfully!',
        life: 6000,
      });
    } catch (error: unknown) {
      console.error('Error generating sample data:', error);

      let errorMessage = 'Failed to generate sample data!';

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
        life: 6000,
      });
    } finally {
      this.isLoading = false;
    }
  }
}
