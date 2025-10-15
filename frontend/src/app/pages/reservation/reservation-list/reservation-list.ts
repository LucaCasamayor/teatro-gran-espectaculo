import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CurrencyPipe, DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ReservationFormComponent } from '../reservation-form/reservation-form';
import { ConfirmDialog } from '../../../../shared/confirm-dialog/confirm-dialog';
import { Reservation } from '../../../core/models/reservation.model';
import { ReservationService } from '../../../core/services/reservation.service';
import { CustomerService } from '../../../core/services/customer.service';
import { EventService } from '../../../core/services/event.service';
import { Customer } from '../../../core/models/customer.model';

@Component({
  selector: 'app-reservation-list',
  standalone: true,
  templateUrl: './reservation-list.html',
  styleUrls: ['./reservation-list.scss'],
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatIconModule,
    MatDialogModule,
    DatePipe,
    FormsModule,
    CurrencyPipe,
    ReservationFormComponent
  ]
})
export class ReservationListComponent implements OnInit, AfterViewInit {
  cols = ['customerName', 'customerEmail', 'eventTitle', 'eventDate', 'tickets', 'total', 'status', 'paidAt'];
  dataSource = new MatTableDataSource<any>([]); // siempre array, nunca undefined
  showForm = false;

  customers: Customer[] = [];
  events: any[] = [];

  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private reservationService: ReservationService,
    private customerService: CustomerService,
    private eventService: EventService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // Cargamos base y luego reservas (si base no está, reintenta)
    this.loadCustomers();
    this.loadEvents();
    this.loadReservations();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  // ------------------ CARGA BASE ------------------

  private loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: (data) => (this.customers = data ?? []),
      error: (err) => console.error('Error cargando clientes:', err)
    });
  }

  private loadEvents(): void {
    this.eventService.getAll().subscribe({
      next: (data) => (this.events = data ?? []),
      error: (err) => console.error('Error cargando eventos:', err)
    });
  }

  // ------------------ CARGA DE RESERVAS ------------------

  private loadReservations(): void {
    // Si aún no están customers/events, reintenta breve
    if (!this.customers?.length || !this.events?.length) {
      setTimeout(() => this.loadReservations(), 200);
      return;
    }

    this.reservationService.getAll().subscribe({
      next: (reservations) => {
        const list = (reservations ?? []).map(r => {
          const customer = this.customers.find(c => c.id === r.customerId);
          const event = this.events.find(e => e.id === r.eventId);
          const ticketSummary = (r.items ?? []).map(i => `${i.quantity} ${i.ticketOptionName}`)
            .join(', ');

          return {
            ...r,
            customerName:
              `${customer?.firstName ?? ''} ${customer?.lastName ?? ''}`.trim() || `Cliente #${r.customerId}`,
            customerEmail: customer?.email ?? '—',
            eventTitle: event?.title ?? `Evento #${r.eventId}`,
            eventDate: event?.startDateTime ?? r.createdAt,
            tickets: ticketSummary
          };
        });

        // PENDIENTE primero, luego por fecha (más próxima primero)
        const sorted = list.sort((a, b) => {
          if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
          if (b.status === 'PENDING' && a.status !== 'PENDING') return 1;
          return new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime();
        });

        // Reasignar array => fuerza update sin detectChanges()
        this.dataSource.data = [...sorted];
      },
      error: (err) => console.error('Error cargando reservas:', err)
    });
  }

  // ------------------ FORM ------------------

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  onReservationCreated(): void {
    this.showForm = false;
    this.loadReservations(); // refresco instantáneo
  }

  // ------------------ ESTADO ------------------

  updatePaymentStatus(row: Reservation, event: MatSelectChange): void {
    const newStatus = event.value;
    const previousStatus = row.status;

    // Si ya está pagado o cancelado, no cambia mas
    if (previousStatus === 'PAID' || previousStatus === 'CANCELLED') {
      event.source.value = previousStatus; // restaura visualmente
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Confirmar cambio de estado',
        message: `¿Deseas marcar esta reserva como "${this.getStatusLabel(newStatus)}"?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean | undefined) => {
      if (!confirmed) {
        // Si no confirma, restaurar el valor original en el select
        event.source.value = previousStatus;
        return;
      }

      //actualizar en backend
      this.reservationService.update(row.id, { status: newStatus }).subscribe({
        next: (updated) => {
          row.status = updated.status;
          row.paidAt = updated.paidAt;
          this.dataSource.data = [...this.dataSource.data]; // fuerza rerender
        },
        error: (err) => {
          console.error('Error actualizando estado:', err);
          event.source.value = previousStatus; // vuelve al estado anterior si falla
        }
      });
    });
  }


  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING': return 'Pendiente';
      case 'PAID': return 'Pagado';
      case 'CANCELLED': return 'Cancelado';
      default: return status;
    }
  }
}
