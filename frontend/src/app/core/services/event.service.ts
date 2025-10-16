import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event } from '../models/event.model';

@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly apiUrl = 'http://localhost:8080/api/events';

  constructor(private http: HttpClient) {}


  getAll(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }


  getById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`);
  }

  create(event: Partial<Event>): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, event);
  }


  update(id: number, event: Partial<Event>): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, event);
  }


  updateStatus(id: number, status: 'SCHEDULED' | 'CANCELLED' | 'FINISHED'): Observable<Event> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<Event>(`${this.apiUrl}/${id}/status`, null, { params });
  }

  getUpcoming(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/upcoming`);
  }
}
