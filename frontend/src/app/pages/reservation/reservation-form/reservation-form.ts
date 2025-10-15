import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { ReservationService } from '../../../core/services/reservation.service';
import { CustomerService } from '../../../core/services/customer.service';
import { EventService } from '../../../core/services/event.service';

@Component({
  selector: 'app-reservation-form',
  standalone: true,
  templateUrl: './reservation-form.html',
  styleUrls: ['./reservation-form.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatSelectModule,
    MatInputModule
  ]
})
export class ReservationFormComponent implements OnInit {
  @Output() saved = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;
  customers: any[] = [];
  events: any[] = [];
  loading = false;

  constructor(
    private fb: FormBuilder,
    private reservationService: ReservationService,
    private customerService: CustomerService,
    private eventService: EventService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      customerId: [null, Validators.required],
      eventId: [null, Validators.required],
      attendeeName: ['', Validators.required],
      ticketOptionId: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });

    this.loadCustomers();
    this.loadEvents();
  }

  loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: (data) => (this.customers = data),
      error: (err) => console.error('Error loading customers:', err)
    });
  }

  loadEvents(): void {
    this.eventService.getAll().subscribe({
      next: (data) => (this.events = data),
      error: (err) => console.error('Error loading events:', err)
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const request = {
      customerId: this.form.value.customerId,
      eventId: this.form.value.eventId,
      attendeeName: this.form.value.attendeeName,
      attendedBy: 'Empleado del teatro',
      items: [
        {
          ticketOptionId: this.form.value.ticketOptionId,
          quantity: this.form.value.quantity
        }
      ]
    };

    this.loading = true;
    this.reservationService.create(request).subscribe({
      next: () => {
        this.loading = false;
        this.saved.emit();
      },
      error: (err) => {
        console.error('Error creating reservation:', err);
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
