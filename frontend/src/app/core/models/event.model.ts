import { TicketOption } from './ticket-option.model';

export class Event {
  id!: number;
  title!: string;
  type!: 'THEATER_PLAY' | 'CONCERT' | 'CONFERENCE';
  startDateTime!: string;
  endDateTime?: string;
  status!: 'OPEN' | 'CLOSED' | 'COMPLETED';
  ticketOptions: TicketOption[] = [];

  constructor(init?: Partial<Event>) {
    Object.assign(this, init);
  }

  get formattedDate(): string {
    return new Date(this.startDateTime).toLocaleString();
  }
}
