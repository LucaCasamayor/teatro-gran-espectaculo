export class Customer {
  id!: number;
  firstName!: string;
  lastName!: string;
  email!: string;
  totalAttendances!: number;
  currentStreak!: number;
  hasFreePass!: boolean;

  constructor(init?: Partial<Customer>) {
    Object.assign(this, init);
  }

  get fullName(): string {
    return `${this.firstName} ${this.lastName}`;
  }
}
