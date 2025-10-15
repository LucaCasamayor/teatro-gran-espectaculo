export class TicketOption {
  id!: number;
  name!: string;
  price!: number;
  capacity!: number;
  sold!: number;

  constructor(init?: Partial<TicketOption>) {
    Object.assign(this, init);
  }

  getRemaining(): number {
    return this.capacity - this.sold;
  }
}
