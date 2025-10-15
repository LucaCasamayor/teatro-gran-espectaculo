export class Customer {
  id!: number;
  name!: string;
  lastname!: string;
  email!: string;
  totalAttendances!: number;
  currentStreak!: number;
  hasFreePass!: boolean;

  constructor(init?: Partial<Customer>) {
    Object.assign(this, init);
  }

  get fullName(): string {
    return `${this.name} ${this.lastname}`;
  }
}
