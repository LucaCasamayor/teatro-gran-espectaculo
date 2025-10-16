import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
  MatDialog
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { EventService } from '../../../core/services/event.service';
import { Event } from '../../../core/models/event.model';
import { ConfirmDialog } from '../../../../shared/confirm-dialog/confirm-dialog';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule
  ],
  templateUrl: './event-form.html',
  styleUrls: ['./event-form.scss']
})
export class EventFormComponent {
  form!: FormGroup;
  isEdit = false;
  minDate = new Date();

  tiposEvento = [
    { value: 'THEATER', label: 'Obra de teatro' },
    { value: 'CONCERT', label: 'Concierto' },
    { value: 'CONFERENCE', label: 'Conferencia' }
  ];

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private dialog: MatDialog,
    private dialogRef: MatDialogRef<EventFormComponent>,
    private cdr: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: { event?: Event }
  ) {}

  ngOnInit(): void {
    this.isEdit = !!this.data?.event;
    this.buildForm();

    if (this.isEdit) {
      this.loadEvent();
    } else {
      this.handleDefaultTickets();
    }
  }

  buildForm(): void {
    this.form = this.fb.group({
      title: ['', Validators.required],
      type: ['', Validators.required],
      startDate: [null, Validators.required],
      startTime: [null, Validators.required],
      endTime: [null, Validators.required], // hora de finalización
      description: [''],
      ticketOptions: this.fb.array([])
    });
  }

  get ticketOptions(): FormArray {
    return this.form.get('ticketOptions') as FormArray;
  }

  handleDefaultTickets(): void {
    const typeControl = this.form.get('type');
    typeControl?.valueChanges.subscribe((type: string) => {
      this.ticketOptions.clear();

      if (!type) return;

      let defaults: string[] = [];

      switch (type) {
        case 'THEATER':
          defaults = ['General', 'VIP'];
          break;
        case 'CONCERT':
          defaults = ['Campo', 'Platea', 'Palco'];
          break;
        case 'CONFERENCE':
          defaults = ['General', 'Meet & Greet'];
          break;
      }

      defaults.forEach(name =>
        this.ticketOptions.push(
          this.fb.group({
            name: [name, Validators.required],
            price: [null, [Validators.required, Validators.min(0)]],
            capacity: [null, [Validators.required, Validators.min(1)]],
            sold: [0],
            available: [0]
          })
        )
      );

      this.cdr.detectChanges();
    });
  }

  loadEvent(): void {
    const e = this.data.event!;
    const start = new Date(e.startDateTime);
    const end = e.endDateTime ? new Date(e.endDateTime) : new Date();


    this.form.patchValue({
      title: e.title,
      type: e.type,
      startDate: start,
      startTime: this.formatTimeForInput(start),
      endTime: this.formatTimeForInput(end),
      description: e.description
    });

    e.ticketOptions.forEach(t => {
      this.ticketOptions.push(
        this.fb.group({
          id: [t.id],
          name: [t.name],
          price: [t.price],
          capacity: [t.capacity],
          sold: [t.sold || 0],
          available: [t.available || 0]
        })
      );
    });
  }

  combineDateTime(date: Date, time: string): string {
    const [hours, minutes] = time.split(':').map(Number);
    const combined = new Date(date);
    combined.setHours(hours, minutes, 0, 0);
    return combined.toISOString();
  }

  formatTimeForInput(date: Date): string {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.hasError(errorName) && (control.dirty || control.touched));
  }

  hasErrorTicket(index: number, controlName: string, errorName: string): boolean {
    const group = this.ticketOptions.at(index) as FormGroup;
    const control = group.get(controlName);
    return !!(control && control.hasError(errorName) && (control.dirty || control.touched));
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.value;
    const start = this.combineDateTime(raw.startDate, raw.startTime);
    const end = this.combineDateTime(raw.startDate, raw.endTime);

    // Validaciones de tiempo
    if (new Date(end) <= new Date(start)) {
      alert('La hora de finalización debe ser posterior a la hora de inicio.');
      return;
    }

    const durationMs = new Date(end).getTime() - new Date(start).getTime();
    if (durationMs > 24 * 60 * 60 * 1000) {
      alert('El evento no puede durar más de 24 horas.');
      return;
    }

    const confirmRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: this.isEdit ? 'Confirmar cambios' : 'Confirmar creación',
        message: this.isEdit
          ? `¿Guardar los cambios del evento "${raw.title}"?`
          : `¿Crear el evento "${raw.title}"?`
      },
      width: '600px'
    });

    confirmRef.afterClosed().subscribe(ok => {
      if (!ok) return;

      const eventData: Partial<Event> = {
        id: this.data?.event?.id,
        title: raw.title,
        type: raw.type,
        startDateTime: start,
        endDateTime: end,
        description: raw.description,
        status: this.isEdit ? this.data.event?.status ?? 'SCHEDULED' : 'SCHEDULED',
        ticketOptions: raw.ticketOptions || []
      };

      const req$ = this.isEdit
        ? this.eventService.update(eventData.id!, eventData)
        : this.eventService.create(eventData);

      req$.subscribe({
        next: () => this.dialogRef.close(true),
        error: err => console.error('Error al guardar evento:', err)
      });
    });
  }
}
