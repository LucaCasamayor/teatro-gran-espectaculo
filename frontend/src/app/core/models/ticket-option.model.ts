export class TicketOption {
  id!: number;
  name!: string;
  price!: number;
  capacity!: number;
  sold!: number;
  available?: number;

  constructor(init?: Partial<TicketOption>) {
    Object.assign(this, init);
  }

  getRemaining(): number {
    return this.available ?? (this.capacity - this.sold);
  }
}
