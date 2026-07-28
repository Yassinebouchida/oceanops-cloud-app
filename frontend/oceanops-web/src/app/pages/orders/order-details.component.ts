import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { OrderService } from '../../services/order.service';

@Component({
    selector: 'app-order-details',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './order-details.component.html',
    styleUrls: ['./order-details.component.scss']
})
export class OrderDetailsComponent implements OnInit {

    order: any = null;
    history: any[] = [];
    comments: any[] = [];

    constructor(
        private route: ActivatedRoute,
        private orderService: OrderService
    ) { }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));

        this.orderService.getById(id).subscribe({
            next: res => this.order = res,
            error: err => console.error(err)
        });

        this.orderService.getHistory(id).subscribe({
            next: res => this.history = res,
            error: err => console.error(err)
        });

        this.orderService.getComments(id).subscribe({
            next: res => this.comments = res,
            error: err => console.error(err)
        });
    }
}