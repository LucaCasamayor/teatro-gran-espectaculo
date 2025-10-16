import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import {MatSelect} from '@angular/material/select';
import {MatOption} from '@angular/material/core';

@Component({
  selector: 'app-generic-table',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    FormsModule,
    MatSelect,
    MatOption
  ],
  templateUrl: './generic-table.html',
  styleUrls: ['./generic-table.scss']
})
export class GenericTableComponent implements OnChanges {
  @Input() dataSource: any[] = [];
  @Input() cols: { key: string; label: string }[] = [];
  @Input() entityName = 'registro';
  @Input() allowDelete = true;

  @Output() edit = new EventEmitter<any>();
  @Output() delete = new EventEmitter<any>();

  searchTerm = '';
  displayedColumns: string[] = [];
  @Output() onStatusChange = new EventEmitter<{ row: any; event: any }>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cols']) {
      this.displayedColumns = (this.cols ?? []).map(c => c.key).concat('actions');
    }
  }

  get filteredData(): any[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) return this.dataSource || [];
    return (this.dataSource || []).filter(row =>
      Object.values(row ?? {}).some(val => String(val ?? '').toLowerCase().includes(term))
    );
  }

  onEdit(row: any) {
    this.edit.emit(row);
  }

  onDelete(row: any) {
    this.delete.emit(row);
  }
}
