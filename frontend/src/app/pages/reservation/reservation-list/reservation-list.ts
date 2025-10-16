import {
  Component,
  OnInit,
  ViewChild,
  AfterViewInit
} from '@angular/core';
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
import { GenericTableComponent } from '../../../../shared/generic-table/generic-table';

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
    FormsModule,
    ReservationFormComponent,
    GenericTableComponent
  ],
  providers: [DatePipe, CurrencyPipe]
})
export class ReservationListComponent implements OnInit, AfterViewInit {
  dataSource = new MatTableDataSource<any>([]);
  showForm = false;
  editingReservation: Reservation | null = null;

  customers: Customer[] = [];
  events: any[] = [];

  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private reservationService: ReservationService,
    private customerService: CustomerService,
    private eventService: EventService,
    private dialog: MatDialog,
    private datePipe: DatePipe,
    private currencyPipe: CurrencyPipe
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.loadEvents();
    this.loadReservations();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

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

  private loadReservations(): void {
    if (!this.customers?.length || !this.events?.length) {
      setTimeout(() => this.loadReservations(), 200);
      return;
    }

    this.reservationService.getAll().subscribe({
      next: (reservations) => {
        const list = (reservations ?? []).map((r) => {
          const customer = this.customers.find((c) => c.id === r.customerId);
          const event = this.events.find((e) => e.id === r.eventId);
          const ticketSummary = (r.items ?? [])
            .map((i) => `${i.quantity} × ${i.ticketOptionName}`)
            .join(', ');

          const baseDate =
            event?.startDateTime || event?.startDate || r.createdAt;

          const eventDateFormatted =
            this.datePipe.transform(baseDate, 'dd/MM/yyyy HH:mm') ?? '—';

          // 🕒 Nueva línea: formatear la fecha de pago si existe
          const paidAtFormatted =
            r.paidAt ? this.datePipe.transform(r.paidAt, 'dd/MM/yyyy HH:mm') : '—';

          return {
            ...r,
            customerName: customer
              ? `${customer.firstName} ${customer.lastName}`
              : '(Cliente eliminado)',
            customerEmail: customer?.email ?? 'Sin correo',
            eventTitle: event?.title ?? `Evento #${r.eventId}`,
            eventDateFormatted,
            paidAtFormatted, // 👈 agregamos esto
            tickets: ticketSummary || '—',
            totalFormatted:
              this.currencyPipe.transform(r.total, 'ARS', 'symbol', '1.0-0') ?? '—',
            statusTranslated: this.translateStatus(r.status)
          };
        });

        // ordenar: pendientes primero, luego fecha
        const sorted = list.sort((a, b) => {
          if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
          if (b.status === 'PENDING' && a.status !== 'PENDING') return 1;
          return (
            new Date(a.eventDateFormatted).getTime() -
            new Date(b.eventDateFormatted).getTime()
          );
        });

        this.dataSource.data = [...sorted];
      },
      error: (err) => console.error('Error cargando reservas:', err)
    });
  }


  // ---------- FORMULARIO ----------
  toggleForm(): void {
    this.showForm = !this.showForm;
    this.editingReservation = null;
  }

  editReservation(row: Reservation): void {
    if (row.status === 'PAID' || row.status === 'CANCELLED') {
      alert('No puedes editar una reserva ya pagada o cancelada.');
      return;
    }

    this.editingReservation = row;
    this.showForm = true;
  }

  onReservationSaved(): void {
    this.showForm = false;
    this.editingReservation = null;


    setTimeout(() => this.loadReservations(), 300);
  }


  reservationColumns = [
    { key: 'customerName', label: 'Cliente' },
    { key: 'customerEmail', label: 'Correo' },
    { key: 'eventTitle', label: 'Evento' },
    { key: 'eventDateFormatted', label: 'Fecha' },
    { key: 'tickets', label: 'Entradas' },
    { key: 'totalFormatted', label: 'Total' },
    { key: 'status', label: 'Pago' },
    { key: 'paidAtFormatted', label: 'Fecha de pago' }
  ];

  updatePaymentStatus(row: Reservation, event: MatSelectChange): void {
    const newStatus = (event.value || '').toUpperCase();
    const previousStatus = row.status;

    if (previousStatus === 'PAID' || previousStatus === 'CANCELLED') {
      event.source.value = previousStatus;
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Confirmar cambio de estado',
        message: `¿Deseas marcar esta reserva como "${this.translateStatus(newStatus)}"?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean | undefined) => {
      if (!confirmed) {
        event.source.value = previousStatus;
        return;
      }

      this.reservationService.updateStatus(row.id, newStatus).subscribe({
        next: () => {
          this.loadReservations();
        },
        error: (err) => {
          console.error('Error actualizando estado:', err);
          alert(err.error?.message || '❌ No se pudo actualizar el estado');
          event.source.value = previousStatus;
        }
      });
    });
  }




  private translateStatus(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'Pendiente';
      case 'PAID':
        return 'Pagado';
      case 'CANCELLED':
        return 'Cancelado';
      default:
        return status;
    }
  }
}
