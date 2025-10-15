export interface ReservationItem {
  id: number;
  ticketOptionId: number;
  ticketOptionName: string;
  quantity: number;
  unitPrice: number;
}

export interface Reservation {
  id: number;
  customerId: number;
  customerName: string;
  eventId: number;
  eventTitle: string;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
  loyaltyFree: boolean;
  total: number;
  createdAt: string;
  paidAt: string | null;
  attendeeName: string;
  attendedBy: string;
  items: ReservationItem[];
}

