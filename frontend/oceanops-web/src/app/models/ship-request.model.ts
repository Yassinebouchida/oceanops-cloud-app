export interface ShipRequestItem {
  id?: number;
  itemName: string;
  quantity: number;
  category: string;
}

export interface ShipRequest {
  id?: number;
  shipId: number;
  shipName?: string;

  clientId: number;
  clientName?: string;

  port: string;
  status: string;
  createdAt: string;

  items: ShipRequestItem[];
}
