import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { CustomerService } from '../../../core/services/customer.service';
import { Customer } from '../../../core/models/customer.model';
import { FieldConfig, GenericFormComponent } from '../../../../shared/generic-form/generic-form';
import { GenericTableComponent } from '../../../../shared/generic-table/generic-table';
import { MatIcon } from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialog} from '../../../../shared/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-customers-list',
  standalone: true,
  imports: [GenericFormComponent, GenericTableComponent, MatIcon],
  templateUrl: './customers-list.html',
  styleUrls: ['./customers-list.scss']
})
export class CustomersListComponent implements OnInit {
  showForm = false;
  dataSource = new MatTableDataSource<Customer>();
  dataToEdit: Customer | null = null;

  customerFormConfig: FieldConfig[] = [
    { controlName: 'firstName', label: 'Nombre', required: true },
    { controlName: 'lastName', label: 'Apellido', required: true },
    { controlName: 'email', label: 'Correo electrónico', type: 'email', required: true }
  ];

  customerColumns = [
    { key: 'firstName', label: 'Nombre' },
    { key: 'lastName', label: 'Apellido' },
    { key: 'email', label: 'Correo' }
  ];

  constructor(private customerService: CustomerService, private dialog : MatDialog) { }

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers() {
    this.customerService.getAll().subscribe(customers => {
      this.dataSource.data = customers;
    });
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) this.dataToEdit = null;
  }

  editCustomer(c: Customer) {
    this.dataToEdit = new Customer({ ...c });
    this.showForm = true;
  }


  createOrUpdateCustomer(data: any) {
    if (this.dataToEdit) {
      this.customerService.update(this.dataToEdit.id, data).subscribe(() => {
        this.loadCustomers();
        this.toggleForm();
      });
    } else {
      this.customerService.create(data).subscribe(() => {
        this.loadCustomers();
        this.toggleForm();
      });
    }
  }

  deleteCustomer(c: Customer) {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '400px',
      data: {
        title: 'Confirmar eliminación',
        message: `¿Estás seguro de que querés eliminar a ${c.firstName} ${c.lastName}?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.customerService.delete(c.id).subscribe(() => this.loadCustomers());
      }
    });
  }

}
