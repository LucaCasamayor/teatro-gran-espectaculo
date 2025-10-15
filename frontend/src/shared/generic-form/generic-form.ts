import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-generic-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './generic-form.html',
  styleUrls: ['./generic-form.scss']
})
export class GenericFormComponent {
  @Input() config!: { label: string; controlName: string; type?: string; required?: boolean }[];
  @Input() form!: FormGroup;
  @Input() loading = false;

  @Output() submitForm = new EventEmitter<void>();
  @Output() cancelForm = new EventEmitter<void>();

  onSubmit(): void {
    this.submitForm.emit();
  }

  onCancel(): void {
    this.cancelForm.emit();
  }
}
