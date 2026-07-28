import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ShipRequestService } from '../../services/ship-request.service';
import { AgentPortuaireService } from '../../services/agent-portuaire.service';

@Component({
  selector: 'app-create-ship-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-ship-request.component.html',
  styleUrls: ['./create-ship-request.component.scss']
})
export class CreateShipRequestComponent {

  // =============================
  // FULL FORM MODEL
  // =============================
  form = {
    port: '',
    shipName: '',
    shipId: '',
    clientName: '',
    clientId: 0,

    agentId: null,

    urgencyLevel: 'NORMAL',
    eta: '',
    requestedDeliveryDate: '',

    notes: '',

    items: [
      { itemName: '', quantity: 1, unit: '', remarks: '' }
    ]
  };

  agents: any[] = [];


  // Attachments stored locally until request is created
  selectedFiles: File[] = [];

  constructor(
    private service: ShipRequestService,
    private agentService: AgentPortuaireService
  ) { }

  // =============================
  // ITEM MANAGEMENT
  // =============================
  addItem() {
    this.form.items.push({
      itemName: '',
      quantity: 1,
      unit: '',
      remarks: ''
    });
  }

  removeItem(index: number) {
    this.form.items.splice(index, 1);
  }

  // =============================
  // FILE SELECTION
  // =============================
  onFilesSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
  }

// =============================
// SUBMIT FORM
// =============================
submit() {

  if (!this.form.agentId) {
    alert("Please select a port agent before submitting the request.");
    return;
  }

  const userRaw = localStorage.getItem('user');

  if (!userRaw) {
    alert("You are not logged in. Please log in again.");
    return;
  }

  const user = JSON.parse(userRaw);

  this.service.createRequest(this.form).subscribe({
    next: (createdRequest: any) => {

      const requestId = createdRequest.id;

      alert("Ship Request Created Successfully!");

      if (this.selectedFiles.length > 0) {
        this.uploadFiles(requestId);
      }

      this.form = {
        port: '',
        shipName: '',
        shipId: '',
        clientName: '',
        clientId: 0,
        agentId: null,
        urgencyLevel: 'NORMAL',
        eta: '',
        requestedDeliveryDate: '',
        notes: '',
        items: [
          {
            itemName: '',
            quantity: 1,
            unit: '',
            remarks: ''
          }
        ]
      };

      this.agents = [];
    },

    error: (err) => {
      console.error(err);
      alert("Failed to create ship request.");
    }
  });
}


// =============================
// UPLOAD FILES
// =============================
uploadFiles(requestId: number) {

  let uploadedCount = 0;

  this.selectedFiles.forEach(file => {

    this.service.uploadAttachment(requestId, file).subscribe({

      next: () => {

        uploadedCount++;

        if (uploadedCount === this.selectedFiles.length) {

          alert("Attachments uploaded successfully!");

          this.selectedFiles = [];
        }

      },

      error: (err) => {
        console.error(err);
        alert(`Failed to upload file: ${file.name}`);
      }

    });

  });

}
// =============================
// LOAD AGENTS BY PORT
// =============================
loadAgents() {

  if (!this.form.port) {
    this.agents = [];
    return;
  }

  this.agentService.getAgentsByPort(this.form.port).subscribe({
    next: (data: any[]) => {

      this.agents = data.filter((agent, index, self) =>
        index === self.findIndex((t) =>
          t.companyName === agent.companyName &&
          t.contact === agent.contact &&
          t.port === agent.port
        )
      );

    },

    error: (err) => {
      console.error(err);
      this.agents = [];
    }
  });

}
}