import { Routes } from '@angular/router';
import {MainLayoutComponent} from './layout/main-layout';
import {Dashboard} from './pages/dashboard/dashboard';


import {ReservationListComponent} from './pages/reservation/reservation-list/reservation-list';
import {CustomersListComponent} from './pages/customers/customer-list/customers-list';
import {EventsListComponent} from './pages/events/event-list/events-list';




export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'events', component: EventsListComponent },
      { path: 'reservations', component: ReservationListComponent },
      { path: 'customers', component: CustomersListComponent },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];


