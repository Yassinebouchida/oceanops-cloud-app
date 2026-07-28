import { Component, OnInit } from '@angular/core';
import { InventoryService } from '../../services/inventory.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.scss'],
})
export class InventoryComponent implements OnInit {
  searchText = '';
  filterCategory = 'all';

  items: any[] = [];

  // Stats
  totalItems = 0;
  lowStockCount = 0;
  outOfStockCount = 0;
  categoriesCount = 0;

  constructor(private inventoryService: InventoryService) { }

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory() {
    this.inventoryService.getAll().subscribe(data => {
      console.log('Received Inventory Data:', data);
      // Map backend model to UI model
      this.items = data.map(item => ({
        id: item.id,
        name: item.name,
        category: item.category,
        stock: item.quantity,
        unit: item.unit,
        price: item.price || 0
      }));
      this.calculateStats();
    });
  }

  viewItem(item: any) {
    alert(`Item Details:\n\nName: ${item.name}\nCategory: ${item.category}\nStock: ${item.stock} ${item.unit}\nPrice: $${item.price}`);
  }

  deleteItem(id: number) {
    if (confirm('Are you unchanged sure you want to delete this item?')) {
      this.inventoryService.delete(id).subscribe(() => {
        this.items = this.items.filter(i => i.id !== id);
        this.calculateStats();
      });
    }
  }

  calculateStats() {
    this.totalItems = this.items.length;
    this.lowStockCount = this.items.filter(i => this.getStatus(i.stock) === 'low').length;
    this.outOfStockCount = this.items.filter(i => this.getStatus(i.stock) === 'out').length;
    this.categoriesCount = new Set(this.items.map(i => i.category)).size;
  }

  // ---------- Filtering ----------
  get filteredItems() {
    return this.items.filter(item =>
      item.name.toLowerCase().includes(this.searchText.toLowerCase()) &&
      (this.filterCategory === 'all' || item.category === this.filterCategory)
    );
  }

  // ---------- Status ----------
  getStatus(stock: number): string {
    if (stock === 0) return 'out';
    if (stock < 50) return 'low'; // Adjusted threshold for larger quantities
    return 'in';
  }
}
