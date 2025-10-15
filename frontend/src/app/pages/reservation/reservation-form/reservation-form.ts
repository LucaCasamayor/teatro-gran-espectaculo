import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule }      from '@angular/material/input';
import { MatSelectModule }     from '@angular/material/select';
import { MatButtonModule }     from '@angular/material/button';
import { MatCardModule }       from '@angular/material/card';
import { MatIconModule }       from '@angular/material/icon';
import { MatSnackBarModule }   from '@angular/material/snack-bar';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

import { CustomerService } from '../../../core/services/customer.service';
import { EventService }    from '../../../core/services/event.service';
import { ReservationService } from '../../../core/services/reservation.service';

import { Event } from '../../../core/models/event.model';
import { TicketOption } from '../../../core/models/ticket-option.model';
import {Reservation} from '../../../core/models/reservation.model';

type Customer = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  active: boolean;
};

@Component({
  selector: 'app-reservation-form',
  standalone: true,
  templateUrl: './reservation-form.html',
  styleUrls: ['./reservation-form.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    // Material
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
  @Output() reservationCreated = new EventEmitter<void>();
  form!: FormGroup;

  // Data
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
      customerId:    [null as number | null, Validators.required],
      attendeeName:  [''],
      eventId:       [null as number | null, Validators.required],
      eventDateTime: new FormControl<string>({ value: '', disabled: true }),
      tickets: this.fb.array([] as FormGroup[])
    });
    this.loadData();

    this.form.get('customerEmail')!.valueChanges.subscribe((value: string) => {
      const v = (value || '').toLowerCase().trim();
      this.filteredCustomers = !v ? this.customers : this.customers.filter(c =>
        c.email.toLowerCase().includes(v) ||
        `${c.firstName} ${c.lastName}`.toLowerCase().includes(v)
      );

      this.form.get('customerId')!.setValue(null);
    });


    this.form.valueChanges.subscribe(() => this.updateTotal());
  }


  // shorthand
  get tickets(): FormArray<FormGroup> {
    return this.form.get('tickets') as FormArray<FormGroup>;
  }

  private loadData(): void {
    this.customerService.getAll().subscribe(cs => {

      this.customers = cs.filter(c => c.active).sort((a, b) => a.email.localeCompare(b.email));
      this.filteredCustomers = this.customers.slice();
    });

    this.eventService.getAll().subscribe(evts => {

      this.events = evts;
    });
  }

  // Mat-autocomplete
  displayEmail = (c: Customer | string | null): string => {
    if (!c) return '';
    return typeof c === 'string' ? c : c.email;
  };

  onCustomerSelected(email: string): void {
    const customer = this.customers.find(c => c.email === email);
    if (customer) {
      this.form.patchValue({
        customerId: customer.id,
        attendeeName: customer.firstName + ' ' + customer.lastName
      });
    }
  }


  onEventChange(eventId: number): void {
    const selected = this.events.find(e => e.id === eventId) || null;
    this.ticketOptions = selected ? selected.ticketOptions : [];
    this.tickets.clear();

    if (selected) {

      this.form.get('eventDateTime')!.enable({ emitEvent: false });
      this.form.get('eventDateTime')!.setValue(new Date(selected.startDateTime).toLocaleString(), { emitEvent: false });


      this.ticketOptions.forEach(opt => {
        this.tickets.push(this.fb.group({
          ticketOptionId: new FormControl<number>(opt.id, { nonNullable: true }),
          name:           new FormControl<string>(opt.name, { nonNullable: true }),
          price:          new FormControl<number>(opt.price, { nonNullable: true }),
          quantity:       new FormControl<number>(0, {
            nonNullable: true,
            validators: [Validators.min(0), Validators.max(opt.available ?? Math.max(opt.capacity - (opt.sold ?? 0), 0))]
          })
        }));
      });
    } else {
      this.form.get('eventDateTime')!.disable({ emitEvent: false });
      this.form.get('eventDateTime')!.reset('', { emitEvent: false });
    }

    this.updateTotal();
  }

  increase(i: number): void {
    const q = this.tickets.at(i).get('quantity') as FormControl<number>;
    const max = (this.tickets.at(i).get('quantity')!.errors?.['max']?.max) ?? Infinity;
    const next = (q.value || 0) + 1;
    q.setValue(Math.min(next, max));
    this.updateTotal();
  }

  decrease(i: number): void {
    const q = this.tickets.at(i).get('quantity') as FormControl<number>;
    const next = (q.value || 0) - 1;
    q.setValue(Math.max(next, 0));
    this.updateTotal();
  }

  private updateTotal(): void {
    const rows = this.tickets.value as { price: number; quantity: number }[];
    this.total = rows.reduce((sum, r) => sum + (r.price * r.quantity), 0);
  }

  onSubmit(): void {
    // Si no completaron attendeeName, usar nombre del cliente
    if (!this.form.value.attendeeName?.trim()) {
      const selected = this.customers.find(c => c.id === this.form.value.customerId);
      if (selected) {
        this.form.get('attendeeName')!.setValue(`${selected.firstName} ${selected.lastName}`);
      }
    }

    if (this.form.invalid || this.total <= 0) return;

    const items = (this.tickets.value as any[])
      .filter((r: any) => r.quantity > 0)
      .map((r: any) => ({
        ticketOptionId: r.ticketOptionId,
        quantity: r.quantity
      }));

    const payload = {
      customerId: this.form.value.customerId,
      eventId: this.form.value.eventId,
      attendeeName: this.form.value.attendeeName,
      attendedBy: '', // lo completa el staff al momento de asistir
      items
    };

    this.reservationService.create(payload).subscribe({
      next: () => {
        alert('Reserva creada correctamente ✅');
        this.reservationCreated.emit();
        this.reset();
      },
      error: () => alert('No se pudo crear la reserva ❌')
    });
  }

  reset(): void {
    this.form.reset();
    this.tickets.clear();
    this.ticketOptions = [];
    this.total = 0;
    this.filteredCustomers = this.customers.slice();
    this.form.get('eventDateTime')!.disable({ emitEvent: false });
  }
}
