import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  MatFormFieldModule
} from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

import { CustomerService } from '../../../core/services/customer.service';
import { EventService } from '../../../core/services/event.service';
import { ReservationService } from '../../../core/services/reservation.service';
import { Event } from '../../../core/models/event.model';
import { TicketOption } from '../../../core/models/ticket-option.model';
import { Reservation } from '../../../core/models/reservation.model';

type Customer = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  active: boolean;
  loyaltyFree?: boolean;
};

@Component({
  selector: 'app-reservation-form',
  standalone: true,
  templateUrl: './reservation-form.html',
  styleUrls: ['./reservation-form.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatSnackBarModule,
    MatAutocompleteModule
  ]
})
export class ReservationFormComponent implements OnInit {
  @Input() reservation: Reservation | null = null;
  @Output() reservationCreated = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;

  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  events: Event[] = [];
  ticketOptions: TicketOption[] = [];

  total = 0;

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private eventService: EventService,
    private reservationService: ReservationService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      customerEmail: ['', [Validators.required, Validators.email]],
      customerId: [null as number | null, Validators.required],
      attendeeName: [''],
      eventId: [null as number | null, Validators.required],
      eventDateTime: new FormControl<string>({ value: '', disabled: true }),
      tickets: this.fb.array([] as FormGroup[])
    });

    this.loadData();
    this.form.valueChanges.subscribe(() => this.updateTotal());
  }

  private loadData(): void {
    this.customerService.getAll().subscribe(cs => {
      this.customers = cs.filter(c => c.active);
      this.filteredCustomers = this.customers.slice();
      if (this.reservation) this.patchReservationData();
    });

    this.eventService.getAll().subscribe(evts => {
      this.events = evts;
      if (this.reservation) this.patchReservationData();
    });
  }

  get tickets(): FormArray<FormGroup> {
    return this.form.get('tickets') as FormArray<FormGroup>;
  }

  // 📅 Al seleccionar evento
  onEventChange(eventId: number): void {
    const selected = this.events.find(e => e.id === eventId) || null;
    this.ticketOptions = selected ? selected.ticketOptions : [];
    this.tickets.clear();

    if (selected) {
      this.form.get('eventDateTime')!.enable({ emitEvent: false });
      this.form
        .get('eventDateTime')!
        .setValue(new Date(selected.startDateTime).toLocaleString(), { emitEvent: false });

      this.ticketOptions.forEach(opt => {
        this.tickets.push(
          this.fb.group({
            ticketOptionId: opt.id,
            name: opt.name,
            price: opt.price,
            quantity: [0, [Validators.min(0), Validators.max(opt.available ?? opt.capacity)]]
          })
        );
      });
    } else {
      this.form.get('eventDateTime')!.disable({ emitEvent: false });
      this.form.get('eventDateTime')!.reset('', { emitEvent: false });
    }

    this.updateTotal();
  }

  // 🔄 Cargar datos al editar
  private patchReservationData(): void {
    if (!this.reservation || !this.customers.length || !this.events.length) return;

    const res = this.reservation;
    const customer = this.customers.find(c => c.id === res.customerId);
    const event = this.events.find(e => e.id === res.eventId);

    this.form.patchValue({
      customerId: res.customerId,
      customerEmail: customer?.email ?? '',
      attendeeName: res.attendeeName,
      eventId: res.eventId
    });

    if (event) {
      this.onEventChange(event.id);
      this.form.get('eventDateTime')!.setValue(
        new Date(event.startDateTime).toLocaleString()
      );

      setTimeout(() => {
        this.tickets.controls.forEach(ctrl => {
          const id = ctrl.get('ticketOptionId')!.value;
          const existing = res.items?.find(i => i.ticketOptionId === id);
          if (existing) ctrl.get('quantity')!.setValue(existing.quantity);
        });
        this.total = res.total;
        this.updateTotal();
      }, 0);
    }
  }

  onCustomerSelected(email: string): void {
    const customer = this.customers.find(c => c.email === email);
    if (customer) {
      this.form.patchValue({
        customerId: customer.id,
        attendeeName: `${customer.firstName} ${customer.lastName}`
      });
    }
  }

  increase(i: number): void {
    const q = this.tickets.at(i).get('quantity') as FormControl<number>;
    const next = (q.value || 0) + 1;
    q.setValue(next);
    this.updateTotal();
  }

  decrease(i: number): void {
    const q = this.tickets.at(i).get('quantity') as FormControl<number>;
    const next = (q.value || 0) - 1;
    q.setValue(Math.max(next, 0));
    this.updateTotal();
  }

  private updateTotal(): void {
    const rows = this.tickets.value as { price?: number; quantity: number }[];
    this.total = rows.reduce((sum, r) => sum + ((r.price ?? 0) * r.quantity), 0);
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const customer = this.customers.find(c => c.id === this.form.value.customerId);
    const event = this.events.find(e => e.id === this.form.value.eventId);

    const items = (this.tickets.value as any[])
      .filter((r: any) => r.quantity > 0)
      .map((r: any) => ({
        ticketOptionId: r.ticketOptionId,
        quantity: r.quantity
      }));

    if (!items.length) {
      alert('Selecciona al menos una entrada.');
      return;
    }

    const payload = {
      customerId: customer?.id ?? this.form.value.customerId,
      eventId: event?.id ?? this.form.value.eventId,
      attendeeName: this.form.value.attendeeName || '',
      attendedBy: '',
      items
    };

    const request$ = this.reservation
      ? this.reservationService.updateFull(this.reservation.id, payload)
      : this.reservationService.create(payload);

    request$.subscribe({
      next: (res) => {
        console.log('✅ Respuesta backend:', res);
        alert(this.reservation ? 'Reserva actualizada ✅' : 'Reserva creada ✅');
        this.reservationCreated.emit();
        this.reset();
        this.cancel.emit();
      },
      error: (err) => {
        console.error('❌ Error en reserva:', err);
        alert('No se pudo guardar la reserva ❌');
      }
    });
  }

  reset(): void {
    this.form.reset();
    this.tickets.clear();
    this.ticketOptions = [];
    this.total = 0;
    this.cancel.emit();
  }
}
