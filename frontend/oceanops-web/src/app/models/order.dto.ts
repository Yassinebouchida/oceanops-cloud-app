export interface OrderDTO {
  id: number,
  clientId: number,
  clientName: string,

  shipId: number,
  shipName: string,

  port: string,

  agentId: number,
  agentName: string,

  description: string,
  status: string,
  createdAt: string,

  attachments?: string | null;
  anomalyDescription?: string;
}
