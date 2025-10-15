import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, MatSidenavModule, MatToolbarModule, MatListModule, MatIconModule, NgIf],
  template: `
  <mat-sidenav-container style="height:100vh">
    <mat-sidenav mode="side" opened>
      <div class="logo">Teatro Gran Espectáculo</div>
      <mat-nav-list>
        <a mat-list-item routerLink="/dashboard"><mat-icon>dashboard</mat-icon>&nbsp;Dashboard</a>
        <a mat-list-item routerLink="/events"><mat-icon>event</mat-icon>&nbsp;Eventos</a>
        <a mat-list-item routerLink="/reservations"><mat-icon>receipt_long</mat-icon>&nbsp;Reservas</a>
        <a mat-list-item routerLink="/customers"><mat-icon>people</mat-icon>&nbsp;Clientes</a>
      </mat-nav-list>
    </mat-sidenav>

    <mat-sidenav-content>
      <mat-toolbar color="primary">
        <span>Panel interno</span>
      </mat-toolbar>
      <div class="content">
        <router-outlet />
      </div>
    </mat-sidenav-content>
  </mat-sidenav-container>
  `,
  styles: [`
    .logo { padding:16px; font-weight:600; }
    .content { padding:24px; }
  `]
})
export class MainLayoutComponent {}

