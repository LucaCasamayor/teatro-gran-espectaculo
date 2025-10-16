import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

export interface FieldConfig {
  controlName: string;
  label: string;
  type?: string;
  required?: boolean;
}

@Component({
  selector: 'app-generic-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule
  ],
  templateUrl: './generic-form.html',
  styleUrls: ['./generic-form.scss']
})
export class GenericFormComponent implements OnInit {
  @Input() config: FieldConfig[] = [];
  @Input() data: any = null;
  @Output() saved = new EventEmitter<any>();
  @Output() cancelled = new EventEmitter<void>();

  form!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    const controls: Record<string, any> = {};
    this.config.forEach(f => {
      controls[f.controlName] = [
        this.data?.[f.controlName] || '',
        f.required ? Validators.required : []
      ];
    });
    this.form = this.fb.group(controls);
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.saved.emit(this.form.value);
      this.form.reset();
    }
  }
}
