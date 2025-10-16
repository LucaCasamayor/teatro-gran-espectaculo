import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import { Reservation } from '../models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.apiUrl);
  }

  getById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/${id}`);
  }

  create(reservation: any): Observable<Reservation> {
    return this.http.post<Reservation>(this.apiUrl, reservation);
  }


  updateStatus(id: number, status: string): Observable<Reservation> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    const body = { status };
    return this.http.patch<Reservation>(`${this.apiUrl}/${id}`, body, { headers });
  }


  updateFull(id: number, data: any): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${id}`, data);
  }


}
