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
  styleUrls: ['./sample-data-component.css']
})
export class SampleDataComponent implements OnInit {
  isLoading = false;
  lastGenerated: Date | null = null;

  constructor(
    private sampleDataService: SampleDataService,
    private messageService: MessageService
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
        detail: response.message || 'Sample data generated successfully!',
        life: 6000
      });
    } catch (error: any) {
      console.error('Error generating sample data:', error);

      const errorMessage = error?.error?.message || 'Failed to generate sample data!';

      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: errorMessage,
        life: 6000
      });
    } finally {
      this.isLoading = false;
    }
  }
}
