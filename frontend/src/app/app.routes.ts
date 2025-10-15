import { Routes } from '@angular/router';
import {MainLayoutComponent} from './layout/main-layout';
import {Dashboard} from './pages/dashboard/dashboard';
import {EventsList} from './pages/events/events-list';
import {CustomersList} from './pages/customers/customers-list';
import {ReservationListComponent} from './pages/reservation/reservation-list/reservation-list';


export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'events', component: EventsList },
      { path: 'reservations', component: ReservationListComponent },
      { path: 'customers', component: CustomersList },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];


