import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { EventService } from '../../../core/services/event.service';
import { Event } from '../../../core/models/event.model';
import { finalize } from 'rxjs';
import {ConfirmDialog} from '../../../../shared/confirm-dialog/confirm-dialog';
import {EventFormComponent} from '../event-form/event-form';

@Component({
  selector: 'app-events-list',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule, MatIconModule],
  templateUrl: './events-list.html',
  styleUrls: ['./events-list.scss']
})
export class EventsListComponent {
  private eventService = inject(EventService);
  private dialog = inject(MatDialog);

  events: Event[] = [];
  loading = false;

  ngOnInit(): void {
    this.loadEvents();
  }


  loadEvents(): void {
    this.loading = true;

    this.eventService
      .getAll()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => {
          this.events = res ?? [];
          this.checkAndUpdateFinishedEvents();
        },
        error: (err) => console.error('Error cargando eventos:', err)
      });
  }

  private checkAndUpdateFinishedEvents(): void {
    const now = new Date();

    this.events.forEach((event) => {
      if (!event.endDateTime) return;

      const end = new Date(event.endDateTime);

      // Si ya finalizó y todavía no está marcado como FINISHED
      if (end < now && event.status !== 'FINISHED') {
        event.status = 'FINISHED';

        // Actualizar en el backend
        this.eventService.updateStatus(event.id!, 'FINISHED').subscribe({
          next: () =>
            console.log(`Evento ${event.id} actualizado a FINISHED automáticamente.`),
          error: (err) =>
            console.error(`Error al actualizar evento ${event.id}:`, err)
        });
      }
    });
  }


  onChangeStatus(event: Event): void {
    const nextStatus = this.getNextStatus(event.status);

    if (!nextStatus) return;

    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Cambiar estado',
        message: `¿Seguro que querés marcar el evento "${event.title}" como ${nextStatus}?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.eventService.updateStatus(event.id!, nextStatus).subscribe({
          next: (updated) => {
            const index = this.events.findIndex((e) => e.id === updated.id);
            if (index !== -1) this.events[index] = updated;
          },
          error: (err) => console.error('Error al cambiar estado', err)
        });
      }
    });
  }


  getNextStatus(
    current: 'SCHEDULED' | 'CANCELLED' | 'FINISHED'
  ): 'SCHEDULED' | 'CANCELLED' | 'FINISHED' | null {
    switch (current) {
      case 'SCHEDULED':
        return 'CANCELLED';
      case 'CANCELLED':
        return 'SCHEDULED';
      case 'FINISHED':
        return null;
      default:
        return null;
    }
  }


  getCardClass(status: string): string {
    switch (status) {
      case 'SCHEDULED':
        return 'card-scheduled';
      case 'CANCELLED':
        return 'card-cancelled';
      case 'FINISHED':
        return 'card-finished';
      default:
        return '';
    }
  }
  onCreate(): void {
    const dialogRef = this.dialog.open(EventFormComponent, {
      width: '700px'
    });

    dialogRef.afterClosed().subscribe((saved) => {
      if (saved) this.loadEvents();
    });
  }

  onEdit(event: Event): void {
    const dialogRef = this.dialog.open(EventFormComponent, {
      width: '700px',
      data: { event }
    });

    dialogRef.afterClosed().subscribe((saved) => {
      if (saved) this.loadEvents();
    });
  }
  translateStatus(status: string): string {
    switch (status) {
      case 'SCHEDULED':
        return 'Programado';
      case 'CANCELLED':
        return 'Cancelado';
      case 'FINISHED':
        return 'Finalizado';
      default:
        return status;
    }
  }

}
