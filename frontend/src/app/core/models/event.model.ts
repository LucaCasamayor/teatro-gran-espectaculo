import { TicketOption } from './ticket-option.model';

export type EventType = 'THEATER' | 'CONCERT' | 'CONFERENCE';
export type EventStatus = 'SCHEDULED' | 'CANCELLED' | 'FINISHED';

export class Event {
  id!: number;
  title!: string;
  type!: EventType;
  startDateTime!: string;
  endDateTime?: string;
  status!: EventStatus;
  description?: string;
  ticketOptions: TicketOption[] = [];

  constructor(init?: Partial<Event>) {
    Object.assign(this, init);
  }
}
