import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {CurrencyPipe, DatePipe, NgClass, NgIf} from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ReservationFormComponent} from '../reservation-form/reservation-form';
import {ConfirmDialog} from '../../../../shared/confirm-dialog/confirm-dialog';
import {Reservation} from '../../../core/models/reservation.model';
import {ReservationService} from '../../../core/services/reservation.service';


@Component({
  selector: 'app-reservation-list',
  standalone: true,
  templateUrl: './reservation-list.html',
  styleUrls: ['./reservation-list.scss'],
  imports: [
    MatTableModule,
    MatSortModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatIconModule,
    MatDialogModule,
    DatePipe,
    NgIf,
    FormsModule,
    CurrencyPipe,
    ReservationFormComponent,
    ConfirmDialog,
    NgClass
  ]
})
export class ReservationListComponent implements OnInit, AfterViewInit {
  cols = ['customerName', 'customerEmail', 'eventTitle', 'eventDate', 'total', 'status', 'paidAt'];
  dataSource = new MatTableDataSource<Reservation>();
  showForm = false;

  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private reservationService: ReservationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  loadReservations(): void {
    this.reservationService.getAll().subscribe({
      next: (data) => {
        const mapped = data.map((r) => ({
          ...r,
          eventDate: (r as any).eventDate ?? r.createdAt
        }));

        mapped.sort((a, b) => {
          if (a.status === 'PENDING' && b.status !== 'PENDING') return -1;
          if (b.status === 'PENDING' && a.status !== 'PENDING') return 1;
          return new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime();
        });

        this.dataSource.data = mapped;
      },
      error: (err) => console.error('Error cargando reservas:', err)
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  onReservationCreated(): void {
    this.toggleForm();
    this.loadReservations();
  }

  updatePaymentStatus(row: Reservation, event: MatSelectChange): void {
    const newStatus = event.value;
    const oldStatus = row.status;

    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Confirmar cambio de estado',
        message: `¿Deseas marcar esta reserva como "${this.getStatusLabel(newStatus)}"?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean | undefined) => {
      if (!confirmed) {
        event.source.writeValue(oldStatus);
        return;
      }

      this.reservationService.update(row.id, { status: newStatus }).subscribe({
        next: () => {
          row.status = newStatus;
          console.log('Estado actualizado en backend:', newStatus);
        },
        error: (err) => {
          console.error('Error actualizando estado:', err);
          event.source.writeValue(oldStatus);
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
